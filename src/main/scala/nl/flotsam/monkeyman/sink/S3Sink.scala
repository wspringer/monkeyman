package nl.flotsam.monkeyman.sink

import nl.flotsam.monkeyman.{SinkFactory, Resource, Sink}
import org.jets3t.service.security.AWSCredentials
import org.jets3t.service.impl.rest.httpclient.RestS3Service
import org.jets3t.service.model.{S3Object, S3Bucket}
import org.jets3t.service.S3Service
import nl.flotsam.monkeyman.util.Closeables._
import java.io.File
import org.apache.commons.io.FileUtils
import nl.flotsam.monkeyman.util.Logging
import org.jets3t.service.acl.{Permission, GroupGrantee}

class S3Sink(service: S3Service, bucket: S3Bucket) extends Sink with Logging {

  private val acl = service.getBucketAcl(bucket)
  acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ)

  def receive(resource: Resource) {
    using (resource.open) {
      in =>
        val tmpFile = File.createTempFile("monkeyman", "tmp")
        try {
          FileUtils.copyInputStreamToFile(resource.open, tmpFile)
          val obj = new S3Object(resource.path)
          obj.setContentType(resource.contentType)
          obj.setDataInputFile(tmpFile)
          obj.setContentLength(FileUtils.sizeOf(tmpFile))
          obj.setAcl(acl)
          info("Generating {}", resource.path)
          service.putObject(bucket, obj)
        } finally {
          tmpFile.delete()
        }
    }
  }

}

object S3Sink extends SinkFactory {

  private val UriPattern = "s3:([^:]+):([^:]+):(.*)".r

  def create(location: String) = {
    location match {
      case UriPattern(accessKey, secretKey, bucketName) =>
        val credentials = new AWSCredentials(accessKey, secretKey)
        val service = new RestS3Service(credentials)
        val bucket = service.getOrCreateBucket(bucketName, S3Bucket.LOCATION_US_STANDARD)
        Some(new S3Sink(service, bucket))
      case _ =>
        None
    }
  }

}
