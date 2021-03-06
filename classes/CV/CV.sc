/*
 A CV models a value constrained by a ControlSpec. The value can be a single Float or an array of Floats.

 Whenever the CV's value changes, it sends a changed message labeled 'synch'.  This way dependants
 (such as GUI objects or server value) can be updated with SimpleControllers.  The method
 		aCV-action_(function)
 creates such a connection.

 A CV's value can be read with the 'value' message.
 CV can also be used as a Pattern (in Pbind) or in combination with other Streams.
*/

CV : Stream {
	classvar <>viewDictionary;

	var <value, <spec;

	*initClass {
		StartUp.add ({ CV.buildViewDictionary })
	}

	*new { | spec = \unipolar, default |
		^super.new.spec_(spec,default);
	}

	action_ { | function | ^SimpleController(this) .put(\synch, function) }

// reading and writing the CV
	value_ { | val |
		value = spec.constrain(val);
		this.changed(\synch, this);
	}
	input_	{ | in | this.value_(spec.map(in)); }
	input 	{ ^spec.unmap(value) }
	asInput 	{ | val | ^spec.unmap(val) }

// setting the ControlSpec
	spec_ 	{ | s, v |
				spec = s.asSpec;
				this.value_(v ? spec.default);
	}
	sp	{ | default= 0, lo = 0, hi=0, step = 0, warp = 'lin' |
		this.spec = ControlSpec(lo,hi, warp, step, default);
	}

	db	{ | default= 0, lo = -100, hi = 20, step = 1, warp = 'lin' |
		this.spec = ControlSpec(lo,hi, warp, step, default);
	}

// split turns a multi-valued CV into an array of single-valued CV's
	split {
		var specs;

		if (spec.size > 1) {
			specs = spec.split;
			^value.collect { |v, i| CV(specs[i], v) }
		}
	}

// Stream and Pattern support
	next { ^value }
	reset {}
	embedInStream { ^value.yield }


// ConductorGUI support
	draw { |win, name =">"|
		if (value.isKindOf(Array) ) {
			~multicvGUI.value(win, name, this);
		} {
			~cvGUI.value(win, name, this);
		}
	}

	*buildViewDictionary {
		var connectDictionary = (
			numberBox:		CVSyncValue,
			slider:			CVSyncInput,
			multiSliderView:	CVSyncMulti,
			popUpMenu:		SVSync,
			listView:			SVSync,
			ezSlider:			CVSyncValue,
			ezNumber:			CVSyncValue,
			knob:			CVSyncInput,
			button:			CVSyncValue,
			textView:		CVSyncText,
			textField:		CVSyncText,
			staticText:		CVSyncText,
		);

		if (GUI.id == \qt) {
			connectDictionary.rangeSlider = CVSyncProps(#[loValue, hiValue]);
			connectDictionary.slider2D = CVSyncProps(#[xValue, yValue]);
		} {
			connectDictionary.rangeSlider = CVSyncProps(#[lo, hi]);
			connectDictionary.slider2D = CVSyncProps(#[x, y]);
		};

		CV.viewDictionary = IdentityDictionary.new;

		GUI.schemes.do { | gui|
			var class;
			#[
				numberBox, slider, rangeSlider, slider2D, multiSliderView,
				popUpMenu, listView, ezSlider, ezNumber,
				knob, button, textView, textField, staticText
			].collect { | name |
				if ( (class = gui.perform(name)).notNil) {
					// regard GUI.schemes changes from SC version 3.7 and up
					if (Main.versionAtLeast(3, 7)) { class = class.superclass };
					CV.viewDictionary.put(class, connectDictionary.at(name))
				}
			}
		};
	}
	connect { | view |
		CV.viewDictionary[view.class].new(this, view) ;
	}

	asControlInput { ^value.asControlInput }
	asOSCArgEmbeddedArray { | array| ^value.asOSCArgEmbeddedArray(array) }

	indexedBy { | key |
		^Pfunc{ | ev | value.at(ev[key] ) }
	}

	windex { | key |
		^Pfunc{ | ev | value.asArray.normalizeSum.windex  }
	}

	at { | index | ^value.at(index) }
	put { | index, val | value = value.putt(index, val) }
	size { ^value.size }


}
