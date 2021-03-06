package org.valkyriercp.rules.constraint.property;

import org.springframework.util.Assert;
import org.valkyriercp.binding.PropertyAccessStrategy;
import org.valkyriercp.rules.constraint.Constraint;
import org.valkyriercp.rules.constraint.Required;

/**
 * Validates a property value as 'required' if some other condition is true.
 *
 * @author Seth Ladd
 * @author Keith Donald
 */
public class RequiredIfTrue extends AbstractPropertyConstraint {

	private Constraint constraint;

	/**
	 * Tests that the property is present if the provided predicate is
	 * satisified.
	 *
	 * @param predicate
	 *            the condition
	 */
	public RequiredIfTrue(String propertyName, Constraint predicate) {
		super(propertyName);
		setConstraint(predicate);
	}

	protected RequiredIfTrue(String propertyName) {
		super(propertyName);
	}

	public Constraint getConstraint() {
		return constraint;
	}

	protected void setConstraint(Constraint predicate) {
		Assert.notNull(predicate, "predicate is required");
		this.constraint = predicate;
	}

    /**
     * Determine if this rule is dependent on the given property name. True if either the
     * direct poperty (from the contstructor) is equal to the given name, or if the "if
     * true" predicate is a PropertyConstraint and it is dependent on the given property.
     * @return true if this rule is dependent on the given property
     */
    public boolean isDependentOn(String propertyName) {
        boolean dependent = false;
        if( getConstraint() instanceof PropertyConstraint ) {
            dependent = ((PropertyConstraint) getConstraint()).isDependentOn( propertyName );
        }
        return super.isDependentOn( propertyName ) || dependent;
    }

	protected boolean test(PropertyAccessStrategy domainObjectAccessStrategy) {
		if (constraint.test(domainObjectAccessStrategy)) {
			return Required.instance().test(
					domainObjectAccessStrategy
					.getPropertyValue(getPropertyName()));
		}

        return true;
	}

	public String toString() {
		return "required if (" + constraint + ")";
	}

}
