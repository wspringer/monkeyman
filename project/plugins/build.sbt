//libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-proguard-plugin" % (v+"-0.1.1"))

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.4")

resolvers += Resolver.url("sbt-plugin-releases",
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

