package org.valkyriercp.rules.constraint;

import java.util.Comparator;

/**
 * Predicate that tests if one comparable object is less than another.
 *
 * @author Keith Donald
 */
public class LessThan extends ComparisonBinaryPredicate implements BinaryConstraint {

	public static LessThan INSTANCE = new LessThan();

	public static synchronized BinaryConstraint instance() {
		return INSTANCE;
	}

	public static void load(LessThan instance) {
		INSTANCE = instance;
	}

	public static BinaryConstraint instance(Comparator c) {
		return new LessThan(c);
	}

	public static Constraint value(Comparable value) {
		return INSTANCE.bind(instance(), value);
	}

	public static Constraint value(Object value, Comparator comparator) {
		return INSTANCE.bind(instance(comparator), value);
	}

	public LessThan() {
		super();
	}

	public LessThan(Comparator comparator) {
		super(comparator);
	}

	protected boolean testCompareResult(int result) {
		return result < 0;
	}

	public String toString() {
		return RelationalOperator.LESS_THAN.toString();
	}

}
