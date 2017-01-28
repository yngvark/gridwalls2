class Main {
    private netcom:Netcom;

    run():void {
        var game = new GameRunner();
        game.run();
        console.log("loading game");

        this.netcom = new Netcom(new ZombieMovedProcessor());
        this.netcom.init(game);
    }

    disconnect():void {
        this.netcom.disconnect();
    }
}