class Main {
    private netcom:Netcom;

    run(divIdName:String, auth:Authentication):void {
        console.log("Running game")

        var game = new GameRunner();
        game.run(divIdName);

        this.netcom = new Netcom(auth, new ZombieMovedProcessor());
        this.netcom.init(game);
    }

    disconnect():void {
        this.netcom.disconnect();
    }
}