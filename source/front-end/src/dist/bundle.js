/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "./game/Main.js");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./game/GameRunner.js":
/*!****************************!*\
  !*** ./game/GameRunner.js ***!
  \****************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\nObject.defineProperty(exports, \"__esModule\", { value: true });\nconst MainScene_1 = __webpack_require__(/*! ./MainScene */ \"./game/MainScene.js\");\nclass GameRunner {\n    constructor() {\n        //\n        // private preload():void {\n        //     this.game.load.image(\"skeleton\", \"img/skeleton.png\");\n        //     this.game.load.image(\"human\", \"img/human.png\");\n        //\n        //     console.log(\"Starting game!!\");\n        //     this.game.add.sprite(1, 1, \"human\");\n        //     document.game = this.game;\n        // }\n        //\n        // private create():void {\n        // }\n        this.zombies = {};\n    }\n    run(divIdName) {\n        console.log(\"Starting game!\");\n        this.scene = new MainScene_1.MainScene();\n        this.game = new Phaser.Game({\n            width: 800,\n            height: 350,\n            type: Phaser.AUTO,\n            scene: this.scene\n        });\n    }\n    zombieMoved(zombieMoved) {\n        if (this.zombies.hasOwnProperty(zombieMoved.id)) {\n            let zombie = this.zombies[zombieMoved.id];\n            console.log(\"Existing zombie:\");\n            console.log(zombie);\n            console.log(zombieMoved);\n            zombie.sprite.setPosition(zombieMoved.coordinate.x * 15, zombieMoved.coordinate.y * 15);\n        }\n        else {\n            let sprite = this.scene.add.sprite(zombieMoved.coordinate.x, zombieMoved.coordinate.y, \"skeleton\");\n            sprite.setScale(0.2, 0.2);\n            let zombie = {\n                id: zombieMoved.id,\n                sprite: sprite\n            };\n            console.log(\"New zombie:\");\n            console.log(zombie);\n            console.log(zombieMoved);\n            this.zombies[zombie.id] = zombie;\n        }\n    }\n}\nexports.GameRunner = GameRunner;\nclass Zombie {\n}\nclass Map {\n    constructor() {\n        this.items = {};\n    }\n    add(key, value) {\n        this.items[key] = value;\n    }\n    has(key) {\n        return key in this.items;\n    }\n    get(key) {\n        return this.items[key];\n    }\n}\n\n\n//# sourceURL=webpack:///./game/GameRunner.js?");

/***/ }),

/***/ "./game/Main.js":
/*!**********************!*\
  !*** ./game/Main.js ***!
  \**********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\nObject.defineProperty(exports, \"__esModule\", { value: true });\nconst GameRunner_1 = __webpack_require__(/*! ./GameRunner */ \"./game/GameRunner.js\");\nconst Netcom_1 = __webpack_require__(/*! ./Netcom */ \"./game/Netcom.js\");\nclass Main {\n    run(divIdName, auth) {\n        console.log(\"Running game\");\n        var game = new GameRunner_1.GameRunner();\n        game.run(divIdName);\n        this.netcom = new Netcom_1.Netcom(auth, new ZombieMovedProcessor());\n        //this.netcom.init(game);\n    }\n    disconnect() {\n        this.netcom.disconnect();\n    }\n}\nexports.Main = Main;\n\n\n//# sourceURL=webpack:///./game/Main.js?");

/***/ }),

/***/ "./game/MainScene.js":
/*!***************************!*\
  !*** ./game/MainScene.js ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\nObject.defineProperty(exports, \"__esModule\", { value: true });\nclass MainScene extends Phaser.Scene {\n    constructor() {\n        super({\n            key: \"MainScene\"\n        });\n    }\n    preload() {\n        // this.load.image(\"logo\", \"./assets/boilerplate/phaser.png\");\n        this.load.image(\"skeleton\", \"img/skeleton.png\");\n        this.load.image(\"human\", \"img/human.png\");\n    }\n    create() {\n        // this.phaserSprite = this.add.sprite(400, 300, \"logo\");\n        this.phaserSprite = this.add.sprite(100, 100, \"human\");\n    }\n}\nexports.MainScene = MainScene;\n\n\n//# sourceURL=webpack:///./game/MainScene.js?");

/***/ }),

/***/ "./game/Netcom.js":
/*!************************!*\
  !*** ./game/Netcom.js ***!
  \************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\nObject.defineProperty(exports, \"__esModule\", { value: true });\nclass Netcom {\n    constructor(auth, zombieMovedProcessor) {\n        this.auth = auth;\n        this.zombieMovedProcessor = zombieMovedProcessor;\n    }\n    init(game) {\n        //var ws = new WebSocket('ws://127.0.0.1:15674/ws');\n        var websocketUrl = 'ws://' + this.auth.host + ':15674/ws';\n        console.log(\"Websocket url: \" + websocketUrl);\n        var ws = new WebSocket(websocketUrl);\n        this.client = Stomp.over(ws);\n        //var client = Stomp.client(ws);\n        //var client = this.client;\n        var _that = this;\n        var on_connect = function () {\n            console.log('connected');\n            console.log(\".-SUBSCRIBE\");\n            var subscription = _that.client.subscribe(\"/exchange/Zombie\", function (msg) {\n                console.log(\"<<< RECEIVED:\");\n                console.log(msg);\n                let zombieMoved = _that.zombieMovedProcessor.process(JSON.parse(msg.body));\n                game.zombieMoved(zombieMoved);\n            });\n            console.log(\".-subscription\");\n            console.log(subscription);\n        };\n        var on_error = function (error) {\n            console.log('error:');\n            console.log(error);\n        };\n        //client.connect('guest', 'guest', on_connect, on_error, '/');\n        console.log(\"Connecting with auth:\");\n        console.log(this.auth);\n        this.client.connect(this.auth.username, this.auth.password, on_connect, on_error, '/');\n    }\n    disconnect() {\n        this.client.disconnect(function () {\n            console.log(\"Disconnected! Cya.\");\n        });\n    }\n}\nexports.Netcom = Netcom;\n\n\n//# sourceURL=webpack:///./game/Netcom.js?");

/***/ })

/******/ });