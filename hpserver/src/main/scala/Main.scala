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

import java.net.URI

import edu.cmu.cs.ls.keymaerax.parser.Parser
import edu.cmu.cs.ls.keymaerax.core._

object MainLaunch {
  def main(args: Array[String]): Unit = {
    val systemIn = System.in
    val systemOut = System.out
    val service = new HPLang()
    val launcher = new Launcher.Builder[LanguageClient]()
      .setInput(systemIn)
      .setOutput(systemOut)
      .setRemoteInterface(classOf[LanguageClient])
      .setLocalService(service)
      .create()
    val client = launcher.getRemoteProxy()
    service.setClient(client)
    launcher.startListening()
    client.logMessage(
      new MessageParams(
        MessageType.Info,
        "hello from scala"
      )
    )
  }
}
class HPLang() {
  private var client: LanguageClient = _
  private var kmxDb: KmxDb = _
  def setClient(c: LanguageClient) {
    client = c;
    kmxDb = new KmxDb(client)
  }
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
    capabilities.setTextDocumentSync(TextDocumentSyncKind.Full)
    val initres = new InitializeResult(capabilities)
    return CompletableFuture.completedFuture(initres)
  }
  @JsonNotification("initialized")
  def initialized(params: InitializedParams): CompletableFuture[Unit] = {
//    throw new Exception("inside initialized")
    return CompletableFuture.completedFuture(())
  }
  @JsonNotification("textDocument/didOpen")
  def didOpen(params: DidOpenTextDocumentParams): CompletableFuture[Unit] = {
    kmxDb.loadDoc(params.getTextDocument())
    CompletableFuture.completedFuture(())
  }
  @JsonNotification("textDocument/didSave")
  def didSave(params: DidSaveTextDocumentParams): CompletableFuture[Unit] = {
    kmxDb.saveDoc(params)
    return CompletableFuture.completedFuture(())
  }

  @JsonRequest("textDocument/hover")
  def hover(params: TextDocumentPositionParams): CompletableFuture[Hover] = {
    val q = params.getPosition().toString()
    //  throw new Exception("inside scala hover")
    val h = new Hover(new MarkupContent("markdown", "We are at position: " + q))
    return CompletableFuture.completedFuture(h)
  }
}
