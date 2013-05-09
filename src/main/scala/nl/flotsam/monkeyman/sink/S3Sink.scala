/*
 * Monkeyman static web site generator
 * Copyright (C) 2013  Wilfred Springer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
