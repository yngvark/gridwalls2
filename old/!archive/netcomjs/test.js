var NetCom = 123;

function should_communicate_with_backend() {
	// Given
	var netCom = new NetCom();
	netCom.connect("ws://172.23.0.3:15674/ws");

	var msg = "";
	netCom.onReceive = function(tmpMsg) {
		msg = tmpMSg;
	}

	// When
	netCom.publish("ManMoveTo 1,5");

	// Then

	// TODO: Somehow use test framework to wait for reply, with a timeout for error handling.
	// waitForMessage(5000);

	expect(msg).toBe("SkeletonAttack 1,5");
}