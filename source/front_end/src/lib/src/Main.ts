class Main {
    private netcom:Netcom;

    run(divIdName:String):void {
        console.log("Running game")

        var game = new GameRunner();
        game.run(divIdName);

        this.netcom = new Netcom(new ZombieMovedProcessor());
        this.netcom.init(game);

    }

    disconnect():void {
        this.netcom.disconnect();
    }
}