package main
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j._
import java.util.concurrent.CompletableFuture
import scala.concurrent._

object MainLaunch {
  def main(args:Array[String]):Unit={
  val systemIn = System.in
  val systemOut = System.out
  val launcher = new Launcher.Builder[LanguageClient]()
    .setInput(systemIn)
    .setOutput(systemOut)
    .setRemoteInterface(classOf[LanguageClient])
    .setLocalService(new foo())
    .create()
  launcher.startListening()
  println("Hello, World!")
  }
}
class foo{
  @JsonRequest("initialize")
  def initialize(
      params: InitializeParams
  ): CompletableFuture[InitializeResult] = {
    val capabilities = new ServerCapabilities()
    capabilities.setHoverProvider(true)
    val initres = new InitializeResult(capabilities)
    return CompletableFuture.completedFuture(initres)
  }
  
  @JsonRequest("textDocument/hover")
  def hover(params: TextDocumentPositionParams): CompletableFuture[Hover] = {
  val h = new Hover(new MarkupContent("","Yay Hover Text"))
  return CompletableFuture.completedFuture(h)
  }
}