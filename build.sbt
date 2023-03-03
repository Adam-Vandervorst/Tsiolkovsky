ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = crossProject(JSPlatform).crossType(CrossType.Pure).withoutSuffixFor(JSPlatform)
  .aggregate(dom, frp, todomvc)
  .settings(
    name := "Tsiolkovsky",
    organization := "be.adamv",
    idePackagePrefix := Some("be.adamv.tsiolkovsky"),
    publish / skip := true
  )

lazy val dom = crossProject(JSPlatform).crossType(CrossType.Pure).withoutSuffixFor(JSPlatform)
  .in(file("tdom"))
//  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(
    name := "Tsiolkovsky-dom",
    organization := "be.adamv",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.0.0",
    scalaJSUseMainModuleInitializer := true,
    publishTo := Some(Resolver.file("local-ivy", file("~")))
  )

lazy val frp = crossProject(JSPlatform).crossType(CrossType.Pure).withoutSuffixFor(JSPlatform)
  .in(file("frp"))
  //  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(
      name := "Tsiolkovsky-reactive",
      organization := "be.adamv",
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.0.0",
      libraryDependencies += "be.adamv" %%% "momentum" % "0.3.0",
      libraryDependencies += "be.adamv" %%% "impuls" % "0.2.2",
      scalaJSUseMainModuleInitializer := true,
      publishTo := Some(Resolver.file("local-ivy", file("~")))
  )

lazy val todomvc = crossProject(JSPlatform).crossType(CrossType.Pure).withoutSuffixFor(JSPlatform)
  .in(file("todomvc"))
  .dependsOn(dom, frp)
  .settings(
    name := "Tsiolkovsky-todo",
    organization := "be.adamv",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.0.0",
    scalaJSUseMainModuleInitializer := true,
    publish / skip := true
  )