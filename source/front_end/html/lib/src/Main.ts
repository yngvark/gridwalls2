class Main {
    private netcom:Netcom;
    private game:GameRunner;

    initGame():void {
        this.game = new GameRunner();
        this.game.run();
    }

    testMove():void {
        this.game.zombieMoved(new ZombieMoved("123", new Coordinate(1, 1)))
    }

    run(server:String):void {
        console.log("loading game");

        this.netcom = new Netcom(new ZombieMovedProcessor());
        this.netcom.init(server, this.game);
    }

    disconnect():void {
        this.netcom.disconnect();
    }
}