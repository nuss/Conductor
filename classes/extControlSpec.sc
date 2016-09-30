+ControlSpec {

	safeHasZeroCrossing {
		var thisMinSign, thisMaxSign;
		#thisMinSign, thisMaxSign = [minval, maxval].collect{ |val|
			if(val.isArray) { val.sign.mean } { val.sign }
		};
		^thisMinSign != thisMaxSign or:{ (thisMinSign == 0).and(thisMaxSign == 0) };
	}

	excludingZeroCrossing {
		if(minval != 0 and:{ maxval != 0 }) {
			^this.hasZeroCrossing
		}
		^false
	}

	// multi-dimensional specs

	size {
		var size = [
			minval.size,
			maxval.size,
			step.size,
			this.default.size
		].maxItem;

		^if(size > 1, { size }, { 1 });
	}

	split {
		var specs, thisMinval, thisMaxval, thisStep, thisDefault, thisGrid;

		if (this.size > 1) {
			specs = Array.newClear(this.size);
			this.size.do { |i|
				if (minval.isArray) { thisMinval = minval.wrapAt(i) } { thisMinval = minval };
				if (maxval.isArray) { thisMaxval = maxval.wrapAt(i) } { thisMaxval = maxval };
				if (step.isArray) { thisStep = step.wrapAt(i) } { thisStep = step };
				if (this.default.isArray) { thisDefault = this.default.wrapAt(i) } { thisDefault = this.default };
				this.grid !? { thisGrid = this.grid };
				specs[i] = ControlSpec(thisMinval, thisMaxval, warp, thisStep, thisDefault, grid);
			}
		} {
			specs = this;
		}

		^specs;
	}

}