TestCV : UnitTest {

	test_new {
		var cv = CV.new;
		this.assertEquals(cv.spec, ControlSpec(0, 1, 'linear', 0.0, 0, ""), "The default CV ControlSpec should be ControlSpec(0, 1, 'linear', 0.0, 0, ""). Actual value: %\n".format(cv.spec));
		this.assertEquals(cv.value, 0, "The CV's value should be 0");
		cv = CV([0, 5].asSpec);
		this.assertEquals(cv.spec, ControlSpec(0, 5, 'linear', 0.0, 0, ""), "An Array, explicitely cast to a ControlSpec by calling asSpec on it, should return a new CV with a valid ControlSpec");
		cv = CV(value: 2);
		this.assertEquals(cv.value, 1.0, "The value of a new CV should be constrained by minval/maxval of its given ControlSpec");
		cv = CV(\freq);
		this.assertEquals(cv.spec, ControlSpec(20, 20000, 'exp', 0, 440, " Hz"), "A ControlSpec given as Symbol should compile to a ControlSpec");
		this.assertFloatEquals(cv.value, 440.0, "The CV's value should automatically have been set to the ControlSpec's default");
		cv = CV(\gfsuu);
		this.assertEquals(cv.spec, ControlSpec(0, 1, 'linear', 0.0, 0, ""), "An invalid spec Symbol should compile to a default \unipolar ControlSpec");
	}

	test_sp {
		var cv = CV.new.sp;
		this.assertEquals(cv.spec, ControlSpec(0, 0, \lin, 0, 0), "The ControlSpec for a CV created from method sp with no arguments given should be: ControlSpec(0, 0, \lin, 0, 0). Actual value: %\n".format(cv.spec));
		cv.sp(default: 56, hi: 100, step: 2, lo: 2, warp: \exp);
		this.assertEquals(cv.spec, ControlSpec(2, 100, \exp, 2, 56), "The ControlSpec for the CV should have been changed through arguments given in CV:-sp");
	}

	test_split {
		var cv = CV([[0, 0, 0], [5, 5, 5], \lin, 0, [2]].asSpec);
		var splitCV = cv.split;
		this.assertEquals(cv.value, [2.0, 2.0, 2.0], "The CV is expected to have a value of [2.0, 2.0, 2.0]");
		this.assertEquals(splitCV.collect(_.value), [2.0, 2.0, 2.0], "The split CV's values should equal 2.0 each");
		this.assertEquals(splitCV.collect(_.spec), [
			ControlSpec(0, 5, \lin, 0, 2),
			ControlSpec(0, 5, \lin, 0, 2),
			ControlSpec(0, 5, \lin, 0, 2)
		], "The split CV's ControlSpecs should result in 3 equal ControlsSpecs");
	}

	test_add_remove_controllers {
		var cv = CV([0, 5, \lin, 0, 0].asSpec);
		var ctrl = cv.addController({ |cv| });
		this.assertEquals(cv.numControllers, 1, "The CV should have 1 action added in a SimpleController");
		this.assertEquals(ctrl.class, SimpleController, "The CV's controller should be of class SimpleController");
		cv.addController({ |cv| });
		this.assertEquals(cv.numControllers, 2, "The CV should have 2 actions added in 2 SimpleControllers");
		cv.removeAllControllers;
		0.1.wait;
		this.assertEquals(cv.numControllers, 0, "All actions and controllers should have been removed from the CV");
	}

	test_value {
		var cv = CV([0, 5, \lin, 0, 2].asSpec);
		cv.addController({ |cv| ~test = cv.value });
		this.assertEquals(cv.value, 2, "The CV's initial value should be 2");
		this.assert(~test.isNil, "~test should be uninitialized: %\n".format(~test));
		cv.value_(4.3);
		this.assertFloatEquals(cv.value, 4.3, "The CV's value should have been set to 4.3");
		this.assertFloatEquals(~test, 4.3, "The value of ~test, addressed in the CV's dependedent action, should be 4.3");
		cv.value_([1, 2, 3]);
		this.assertEquals(cv.value, [1, 2, 3], "Setting a CV's value to an array of numbers will multichannel-expand the CV");
		cv.removeAllControllers;
		this.assertEquals(cv.numControllers, 0, "The CV's dependents should have been removed");
		~test = nil;
	}

	test_input {
		var cv = CV([0, 5, \lin, 0, 2].asSpec);
		cv.addController({ |cv| ~test = cv.input });
		this.assertFloatEquals(cv.input, 2/5, "The CV's initial input should be 2");
		this.assert(~test.isNil, "~test should be uninitialized : %\n".format(~test));
		cv.input_(0.231);
		this.assertFloatEquals(cv.input, 0.231, "The CV's input should have been set to 0.231");
		this.assertFloatEquals(~test, 0.231, "The value of ~test, addressed in the CV's dependedent action, should be 0.231");
		cv.removeAllControllers;
		this.assertEquals(cv.numControllers, 0, "The CV's dependents should have been removed");
		~test = nil;
	}

	test_asInput {
		var cv = CV([0, 5, \lin, 0, 2].asSpec);
		var input = cv.asInput(4);
		this.assertFloatEquals(input, cv.spec.unmap(4), "Calling asInput on the CV should return its unmapped equivalent determined by the CV's ControlSpec")
	}

	test_default {
		var cv = CV([0, 5, \lin, 0, 2].asSpec);
		cv.default_(3);
		this.assertEquals(cv.value, 3, "Calling default_ on the CV should set its value");
		this.assertEquals(cv.spec.default, 3, "Calling default_ on the CV should set its ControlSpec's default");
		cv.default_(-9);
		this.assertEquals(cv.value, 0.0, "Calling default_ with a value outside the ControlSpec's constraints should set the value to the ControlSpec's minval or maxval");
		this.assertEquals(cv.spec.default, 3, "Calling default_ with a value outside the ControlSpec's constraints should leave the ControlSpec's default untouched")
	}

	test_next {
		var cv = CV([0, 5, \lin, 0, 2].asSpec);
		this.assertFloatEquals(cv.next, 2.0, "Calling next on a CV should return its value")
	}

	test_embedInStream {
		var cv = CV([0, 5, \lin, 0, 2].asSpec);
		var stream;
		// tests are running in a Routine themselves?
		// this.assertException({ cv.embedInStream }, PrimitiveFailedError, "Trying to call embedInStream outside a Routine should throw a PrimitiveFailedError");
		stream = Routine { cv.embedInStream };
		this.assertFloatEquals(stream.next, 2.0, "embedInStream should yield the CV's value");
		cv.value_(3.421);
		this.assertEquals(stream.next, nil, "embedInStream can only yield once");
	}

	test_buildViewDictionary {
		this.assertEquals(CV.viewDictionary.class, IdentityDictionary, "Class CV's viewDictionary should be an IdentityDictionary");
		this.assert(CV.viewDictionary.notEmpty, "Class CV's viewDictionary should not be empty");
	}

}