import {Main} from "./Main";
import {Authentication} from "./Authentication";

class Index {
    private main:Main = new Main();
    
    connect() {
        let host:String = (document.getElementById('host') as any).value;
        let username:String = (document.getElementById('username') as any).value;
        let password:String = (document.getElementById('password') as any).value;
        let auth = new Authentication();
        auth.host = host;
        auth.username = username;
        auth.password = password;

        console.log("Connect to " + host + " with u/p: " + username + "/" + password);
        this.main.run(auth);

        //document.getElementById('disconnect').style.display = 'block'
        // document.getElementById('connect').style.display = 'none'

    }

    disconnect() {
        console.log("Disconnect")
        this.main.disconnect();
    }
}

document.addEventListener('DOMContentLoaded', function() {
    console.log("DOMContentLoaded");

    var index = new Index();

    (document.getElementById("connect") as any).addEventListener('click', function(e) {
        index.connect();
    });

    (document.getElementById("disconnect") as any).addEventListener('click', function(e) {
        index.disconnect();
    });

}, false);

