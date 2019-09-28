package main
import org.eclipse.lsp4j.TextDocumentItem
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.services.LanguageClient
import edu.cmu.cs.ls.keymaerax.parser.KeYmaeraXLexer
import edu.cmu.cs.ls.keymaerax.parser.KeYmaeraXLexer.TokenStream
import edu.cmu.cs.ls.keymaerax.parser.KeYmaeraXParser
import edu.cmu.cs.ls.keymaerax.parser.{Location => KmxLoc}
import edu.cmu.cs.ls.keymaerax.parser.ParseException
import scala.collection.mutable
import java.net.URI
import edu.cmu.cs.ls.keymaerax.core.Expression
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.PublishDiagnosticsParams
import scala.collection.JavaConversions._
import org.eclipse.lsp4j.TextDocumentContentChangeEvent
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.MessageType
import StrExts._

case class Semantics(
    ts: KeYmaeraXLexer.TokenStream,
    abs: Option[Expression]
)
class KmxDb(c: LanguageClient) {
  val docDict: mutable.Map[String, Semantics] = mutable.Map()
  def validateText(uri:String, text:String) = {
    val lexemes = KeYmaeraXLexer(text)
    val expr = try {
        c.publishDiagnostics(
          new PublishDiagnosticsParams(uri, List())
        )
      Some(KeYmaeraXParser.parse(lexemes))
    } catch {
      case e: ParseException => {
        val diagnostic = List(expToDiagnostic(e))
        c.publishDiagnostics(
          new PublishDiagnosticsParams(uri, diagnostic)
        )
        None
      }
    }
    docDict(uri)= Semantics(lexemes, expr)
  }
  def saveDoc(d: DidSaveTextDocumentParams): Unit = {
      validateText(d.getTextDocument.getUri, readFile(d.getTextDocument))
  }
  def loadDoc(d: TextDocumentItem): Unit = {
      validateText(d.getUri(),d.getText())
  }
  private def expToDiagnostic(e: ParseException): Diagnostic = {
    return new Diagnostic(
      kmxLocToLspRange(e.loc),
      makeDiagnosticText(e),
      DiagnosticSeverity.Error,
      "KeYmaera X"
    )
  }
  private def makeDiagnosticText(e: ParseException): String = {
    val base = s"""Parsing problem: ${e.msg}.
                   |${e.expect}"""".stripMargin
    if (e.hint != null && e.hint.nonEmpty) {
      return base + s"\nHint: ${e.hint}"
    } else {

      return base
    }
  }

  private def kmxLocToLspRange(l: KmxLoc): Range = {
    return new Range(
      //vscode is 0 indexed here. KeYmaera is not.
      //Also, vscode is exclusive on the left. KeYmaera is not
      new Position(l.begin.line - 1, Math.max(l.begin.column - 1, 0)),
      new Position(l.end.line - 1, l.end.column)
    )
  }
}
