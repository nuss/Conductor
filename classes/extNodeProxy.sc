+NodeProxy {
	set { arg ... args; // pairs of keys or indices and value
		nodeMap.set(*args);
		if(this.isPlaying) {
			server.sendBundle(server.latency, [15, group.nodeID] ++ args.asOSCArgArray);
		}
	}

	prset { arg ... args; // pairs of keys or indices and value
		nodeMap.set(*args);
		if(this.isPlaying) {
			server.sendBundle(server.latency, [15, group.nodeID] ++ args.asOSCArgArray);
		};
	}

	// hmmm... needed?
	/*prSetControls { | kvArray |
		kvArray.buildCVConnections({ | label, expr| this.prset(label, expr.value)})
	}*/
}
