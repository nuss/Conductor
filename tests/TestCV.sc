TestCV : UnitTest {

	test_new_default {
		var cv = CV.new;
		this.assertEquals(cv.spec, ControlSpec(0, 1, 'linear', 0.0, 0, ""), "The default CV ControlSpec should be ControlSpec(0, 1, 'linear', 0.0, 0, ""). Actual value: %\n".format(cv.spec));
	}

	test_sp {
		var cv = CV.new.sp;
		this.assertEquals(cv.spec, ControlSpec(0, 0, \lin, 0, 0), "The ControlSpec for a CV created from method sp with no arguments given should be: ControlSpec(0, 0, \lin, 0, 0). Actual value: %\n".format(cv.spec));
	}

}