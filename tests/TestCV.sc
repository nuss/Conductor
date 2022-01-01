TestCV : UnitTest {

	test_new_default {
		var cv = CV.new;
		this.assertEquals(cv.spec, ControlSpec(0, 1, 'linear', 0.0, 0, ""), "The default CV ControlSpec should be ControlSpec(0, 1, 'linear', 0.0, 0, ""). Actual value: %\n".format(cv.spec));
	}

	test_sp {
		var cv = CV.new.sp;
		this.assertEquals(cv.spec, ControlSpec(0, 0, \lin, 0, 0), "The ControlSpec for a CV created from method sp with no arguments given should be: ControlSpec(0, 0, \lin, 0, 0). Actual value: %\n".format(cv.spec));
		cv.sp(default: 56, hi: 100, step: 2, lo: 2, warp: \exp);
		this.assertEquals(cv.spec, ControlSpec(2, 100, \exp, 2, 56), "The ControlSpec for the CV should have been changed through arguments given in CV:-sp");
	}

	test_split {
		var cv = CV([[0, 0, 0], [5, 5, 5], \lin, 0, 2].asSpec, [2, 2, 2]);
		var splitCV = cv.split;

		this.assertEquals(splitCV, [
			CV(ControlSpec(0, 5, \lin, 0, 2), 2),
			CV(ControlSpec(0, 5, \lin, 0, 2), 2),
			CV(ControlSpec(0, 5, \lin, 0, 2), 2)
		], "The 3-dimensional CV should have been split up into 3 separate 1-dimensional CVs")
	}

}