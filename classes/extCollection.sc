+Collection {

	depth {
		var depth = 0;
		var func = { |col|
			var cols = col.select { |it| it.isCollection };
			if (cols.size > 0) {
				depth = depth + 1;
				cols.do { |it|
					func.(it)
				}
			} { depth }
		};
		func.(this);
		^depth
	}

}