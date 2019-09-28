package main

import org.eclipse.lsp4j.TextDocumentIdentifier
import java.net.URI

class StringExts {
  implicit class XtensionString(value: String) {

    /** Returns true if this is a Scala.js or Scala Native target
      *
      * FIXME: https://github.com/scalacenter/bloop/issues/700
      */
    def isNonJVMPlatformOption: Boolean = {
      def isCompilerPlugin(name: String, organization: String): Boolean = {
        value.startsWith("-Xplugin:") &&
        value.contains(name) &&
        value.contains(organization)
      }
      // Scala Native and Scala.js are not needed to navigate dependency sources
      isCompilerPlugin("nscplugin", "scala-native") ||
      isCompilerPlugin("scalajs-compiler", "scala-js") ||
      value.startsWith("-P:scalajs:")
    }

    def lastIndexBetween(
        char: Char,
        lowerBound: Int = 0,
        upperBound: Int = value.size
    ) = {
      var index = upperBound
      while (index > lowerBound && value(index) != char) {
        index -= 1
      }
      index
    }
  }
}
object StrExts {

  def readFile(d: TextDocumentIdentifier): String = {
    val path = URI.create(d.getUri).getPath()
    val source = scala.io.Source.fromFile(path)
    try source.mkString
    finally source.close()
  }
}
