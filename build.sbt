libraryDependencies ~= { seq =>
  val vers = "0.8.7"
  seq ++ Seq(
    "net.databinder" %% "dispatch-core" % vers,
    "net.databinder" %% "dispatch-oauth" % vers,
    "net.databinder" %% "dispatch-nio" % vers,
    "net.databinder" %% "dispatch-http" % vers,
    "net.liftweb" %% "lift-json" % "2.4"
  )
}

initialCommands := "import dispatch._;import net.liftweb.json._"