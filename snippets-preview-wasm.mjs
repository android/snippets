
import "./custom-formatters.js"
import { importObject, setWasmExports } from './snippets-preview-wasm.import-object.mjs'

let wasmInstance;

const isNodeJs = (typeof process !== 'undefined') && (process.release.name === 'node');
const isDeno = !isNodeJs && (typeof Deno !== 'undefined')
const isStandaloneJsVM =
    !isDeno && !isNodeJs && (
        typeof d8 !== 'undefined' // V8
        || typeof inIon !== 'undefined' // SpiderMonkey
        || typeof jscOptions !== 'undefined' // JavaScriptCore
    );
const isBrowser = !isNodeJs && !isDeno && !isStandaloneJsVM && (typeof window !== 'undefined' || typeof self !== 'undefined');

if (!isNodeJs && !isDeno && !isStandaloneJsVM && !isBrowser) {
  throw "Supported JS engine not detected";
}

const wasmFilePath = './snippets-preview-wasm.wasm';
const wasmOptions = { builtins: ['js-string'], importedStringConstants: "'" }

try {
  if (isNodeJs) {
    const module = await import(/* webpackIgnore: true */'node:module');
    const importMeta = import.meta;
    const require = module.default.createRequire(importMeta.url);
    const fs = require('fs');
    const url = require('url');
    const filepath = import.meta.resolve(wasmFilePath);
    const wasmBuffer = fs.readFileSync(url.fileURLToPath(filepath));
    const wasmModule = new WebAssembly.Module(wasmBuffer, wasmOptions);
    wasmInstance = new WebAssembly.Instance(wasmModule, importObject);
  }

  if (isDeno) {
    const path = await import(/* webpackIgnore: true */'https://deno.land/std/path/mod.ts');
    const binary = Deno.readFileSync(path.fromFileUrl(import.meta.resolve(wasmFilePath)));
    const module = await WebAssembly.compile(binary, wasmOptions);
    wasmInstance = await WebAssembly.instantiate(module, importObject);
  }

  if (isStandaloneJsVM) {
    const importMeta = import.meta;
    const filepath = importMeta.url.replace(/\.mjs$/, '.wasm');
    const wasmBuffer = read(filepath, 'binary');
    const wasmModule = new WebAssembly.Module(wasmBuffer, wasmOptions);
    wasmInstance = new WebAssembly.Instance(wasmModule, importObject);
  }

  if (isBrowser) {
    wasmInstance = (await WebAssembly.instantiateStreaming(fetch(new URL('./snippets-preview-wasm.wasm',import.meta.url).href), importObject, wasmOptions)).instance;
  }
} catch (e) {
  if (e instanceof WebAssembly.CompileError) {
    let text = `Please make sure that your runtime environment supports the latest version of Wasm GC and Exception-Handling proposals.
For more information, see https://kotl.in/wasm-help
`;
    if (isBrowser) {
      console.error(text);
    } else {
      const t = "\n" + text;
      if (typeof console !== "undefined" && console.log !== void 0)
        console.log(t);
      else
        print(t);
    }
  }
  throw e;
}

const exports = wasmInstance.exports
setWasmExports(exports);
exports._start();

export const {
    memory,
    _start
} = exports

