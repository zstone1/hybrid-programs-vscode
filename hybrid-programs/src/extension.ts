// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode';
import * as path from 'path';
import {
	LanguageClient,
	LanguageClientOptions,
	ServerOptions,
	TransportKind,
	Executable
} from 'vscode-languageclient';


let client: LanguageClient;
// this method is called when your extension is activated
// your extension is activated the very first time the command is executed
export function activate(context: vscode.ExtensionContext) {
    console.log("in activate");
	// Use the console to output diagnostic information (console.log) and errors (console.error)
	// This line of code will only be executed once when your extension is activated
	launchLSPClient(context);
	// The command has been defined in the package.json file
	// Now provide the implementation of the command with registerCommand
	// The commandId parameter must match the command field in package.json
	let disposable = vscode.commands.registerCommand('extension.hybrid-programs', () => {
		// The code you place here will be executed every time your command is executed

		// Display a message box to the user
		vscode.window.showInformationMessage('sup nerds!');
	});

	context.subscriptions.push(disposable);

}
export function deactivate(): Thenable<void> {
	if (!client) {
		return Promise.resolve(undefined);
	}
	else{
	return client.stop();
	}
  }
function launchLSPClient(context: vscode.ExtensionContext) {
	console.log("in launch client");
	let p = context.asAbsolutePath(path.join('hpserver','target','universal','stage','bin','hpserver.bat'));
	let serverRun :Executable = {
        command: p
	};
	// If the extension is launched in debug mode then the debug server options are used
	// Otherwise the run options are used
	let serverOptions: ServerOptions = {
		run: serverRun,
		debug: serverRun
	};
	// Options to control the language client
	let clientOptions: LanguageClientOptions = {
		// Register the server for plain text documents
		documentSelector: [{ scheme: 'file', language: 'hybrid-program' }]
	};
	// Create the language client and start the client.
	client = new LanguageClient('languageServerExample', 'Language Server Example', serverOptions, clientOptions);

	// Start the client. This will also launch the server
	client.start();
}