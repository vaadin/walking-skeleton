package com.example.application.security.domain.jpa;

import com.example.application.security.domain.UserId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jspecify.annotations.Nullable;

/**
 * JPA attribute converter for {@link UserId} domain primitives.
 * <p>
 * This converter enables seamless persistence of {@link UserId} objects in JPA entities by converting them to and from
 * their string representation in the database. It allows entity fields of type {@link UserId} to be automatically
 * converted to {@code VARCHAR} columns and vice versa.
 * </p>
 * <p>
 * The converter handles null values gracefully, converting null {@link UserId} objects to null database values and null
 * database values back to null {@link UserId} objects.
 * </p>
 * <p>
 * Example usage in JPA entities: <!-- spotless:off -->
 * <pre>
 * {@code
 * @Entity
 * public class User {
 *     @Convert(converter = UserIdAttributeConverter.class)
 *     private UserId userId;
 *
 *     // Or use autoApply if configured globally:
 *     // private UserId userId; // Automatically converted
 * }
 * }
 * </pre>
 * <!-- spotless:on -->
 * </p>
 * <p>
 * To apply this converter automatically to all {@link UserId} fields without explicit {@code @Convert} annotations, add
 * {@code autoApply = true} to the {@code @Converter} annotation.
 * </p>
 *
 * @see UserId The domain primitive this converter handles
 * @see AttributeConverter The JPA interface this class implements
 * @see jakarta.persistence.Convert The annotation used to apply this converter to entity fields
 */
@Converter
public class UserIdAttributeConverter implements AttributeConverter<UserId, String> {

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable UserId userId) {
        return userId == null ? null : userId.toString();
    }

    @Override
    public @Nullable UserId convertToEntityAttribute(@Nullable String s) {
        return s == null ? null : UserId.of(s);
    }
}
