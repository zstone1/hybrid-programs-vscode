package main
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j._
import java.util.concurrent.CompletableFuture
import scala.concurrent._
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.services.TextDocumentService

object MainLaunch {
  def main(args:Array[String]):Unit={
  val systemIn = System.in
  val systemOut = System.out
  val launcher = new Launcher.Builder[LanguageClient]()
    .setInput(systemIn)
    .setOutput(systemOut)
    .setRemoteInterface(classOf[LanguageClient])
    .setLocalService(new HPLang())
    .create()
  launcher.startListening()
  }
}
class HPLang {
  @JsonNotification("exit")
  def exit(): Unit = ()
  
  @JsonRequest("shutdown")
  def shutdown(): CompletableFuture[Object] = {
    CompletableFuture.completedFuture(new Object())
  }

  @JsonRequest("initialize")
  def initialize(
      params: InitializeParams
  ): CompletableFuture[InitializeResult] = {
    val capabilities = new ServerCapabilities()
    capabilities.setHoverProvider(true)
    val initres = new InitializeResult(capabilities)
    return CompletableFuture.completedFuture(initres)
  }
  @JsonNotification("initialized")
  def initialized(params: InitializedParams): CompletableFuture[Unit] = {
//    throw new Exception("inside initialized")
    return CompletableFuture.completedFuture(())
  }

  @JsonRequest("textDocument/hover")
  def hover(params: TextDocumentPositionParams): CompletableFuture[Hover] = {
  //  throw new Exception("inside scala hover")
  val h = new Hover(new MarkupContent("markdown", "yay hover text 2"))
  return CompletableFuture.completedFuture(h)
  }
}

