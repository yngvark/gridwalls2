import {ZombieMoved} from "./ZombieMoved";
import {Coordinate} from "./Coordinate";

export class ZombieMovedProcessor {
    process(msg:any):ZombieMoved {
        var id = "123";
        var coordinate = new Coordinate(msg.x, msg.y);
        var zombieMoved = new ZombieMoved(id, coordinate);

        return zombieMoved
    }
}