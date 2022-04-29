(ns bedrock.utils.jetty-logging
  (:import org.eclipse.jetty.util.log.Log))

(.setProperty (org.eclipse.jetty.util.log.Log/getProperties) "org.eclipse.jetty.util.log.announce" "false")
