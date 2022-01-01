CVSpecs {
	classvar specs;

	*initClass {
		Class.initClassTree(Server);
		Class.initClassTree(ServerOptions);

		StartUp.add ({
			specs = (
				// set up some ControlSpecs for common mappings
				// you can add your own after the fact.

				unipolar: 	ControlSpec(0, 1),
				bipolar: 	ControlSpec(-1, 1, default: 0),

				i_out:		ControlSpec(0, 1023, 'lin', 1, 0),
				out:		ControlSpec(0, 1023, 'lin', 1, 0),

				freq: 		ControlSpec(20, 20000, \exp, 0, 440, units: " Hz"),
				lofreq: 	ControlSpec(0.1, 100, \exp, 0, 6, units: " Hz"),
				midfreq: 	ControlSpec(25, 4200, \exp, 0, 440, units: " Hz"),
				widefreq: 	ControlSpec(0.1, 20000, \exp, 0, 440, units: " Hz"),
				phase: 		ControlSpec(0, 2pi),
				rq: 		ControlSpec(0.001, 2, \exp, 0, 0.707),

				audiobus: 	ControlSpec(0, 127, step: 1),
				controlbus: ControlSpec(0, 4095, step: 1),
				audioin: 	ControlSpec(0, Server.default.options.firstPrivateBus-1, step: 1),
				privatein: 	ControlSpec(Server.default.options.firstPrivateBus, 4095, step: 1),
				in: 		ControlSpec(0, 4095, step: 1),
				fin: 		ControlSpec(0, 4095, step: 1),

				midi: 		ControlSpec(0, 127, default: 64),
				midinote: 	ControlSpec(0, 127, default: 60),
				midivelocity: ControlSpec(1, 127, default: 64),


				dbamp: 		ControlSpec(0.ampdb, 1.ampdb, \db, units: " dB"),
				amp: 		ControlSpec(0, 1, \amp, 0, 0),
				boostcut: 	ControlSpec(-20, 20, units: " dB",default: 0),
				db: 		ControlSpec(-100, 20, default: -20),

				pan: 		ControlSpec(-1, 1, default: 0),
				detune: 	ControlSpec(-20, 20, default: 0, units: " Hz"),
				rate: 		ControlSpec(0.125, 8, \exp, 0, 1),
				beats: 		ControlSpec(0, 20, units: " Hz"),
				ratio: 		ControlSpec(1/64, 64, \exp, 0, 1),
				dur: 		ControlSpec(0.01, 10, \exp, 0, 0.25),

				delay: 		ControlSpec(0.0001, 1, \exp, 0, 0.3, units: " secs"),
				longdelay: 	ControlSpec(0.001, 10, \exp, 0, 0.3, units: " secs"),

				fadeTime: 	ControlSpec(0.001, 10, \exp, 0, 0.3, units: " secs")
			);

			specs.pairsDo { |k, v| Spec.add(k, v) }
		})
	}

	*findSpec { |name|
		var spec = specs[name.asSymbol];
		spec ?? {
			spec = specs[name.asString.select(_.isAlpha).asSymbol]
		};
		^spec;
	}

	*addSpec { |name, spec|
		specs[name.asSymbol] = spec;
	}

}