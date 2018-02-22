class ZombieMovedProcessor {
    private regex = /([a-z]+)=([a-zA-Z0-9,-]+)/g;

    process(msg:any):ZombieMoved {
        /*
        var matches = this.regex.exec(msg);
        var move:any = {};
        while (matches != null) {
            move[matches[1]] = matches[2];
            matches = this.regex.exec(msg);
        }
        console.log(move);

        var id:string = move.id;
        //and: "MESSAGE", headers: {…}, body: "{"x":8,"y":4}", ack: ƒ, nack: ƒ}

        var coords:Array<any> = move.tc.split(",");
        var coordinate = new Coordinate(coords[0], coords[1]);
        */

        var id = "123"
        var coordinate = new Coordinate(msg.x, msg.y)
        
        return new ZombieMoved(id, coordinate);
    }
}