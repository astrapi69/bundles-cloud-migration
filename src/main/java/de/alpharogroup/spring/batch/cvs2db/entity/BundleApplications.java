/**
 * The MIT License
 *
 * Copyright (C) 2007 - 2015 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *  *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *  *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.alpharogroup.spring.batch.cvs2db.entity;

import de.alpharogroup.db.entity.BaseEntity;
import de.alpharogroup.db.entity.enums.DatabasePrefix;
import de.alpharogroup.db.entity.name.NameEntity;
import de.alpharogroup.db.entity.name.versionable.VersionableNameUUIDEntity;
import de.alpharogroup.hibernate.generator.IdentifiableSequenceStyleGenerator;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The entity class {@link BundleApplications} is the root of every bundle application. Every entity
 * of type {@link BundleNames} has as reference to a {@link BundleApplications}. Every entity class
 * of {@link BundleApplications} has exactly one default locale that is mandatory. If you see it
 * from the properties file view it is the default properties file is the properties file without
 * the locale suffix. The entity class {@link BundleApplications} acts as the manager of a
 * corresponding real application like a web application or an android-app. If the real application
 * supports only one locale the default locale is enough but if the real application supports more
 * than one locale object then the supported locale objects are kept in the list
 * 'supportedLocales'.<br>
 * Note: The supported locale objects for a real application are mandatory as the default locale.
 */
@Entity
@NamedQueries({
		@NamedQuery(name = BundleApplications.NQ_FIND_SUPPORTED_LANGUAGE_LOCALE, query = "select ba from BundleApplications ba where :languageLocale member of ba.supportedLocales"),
		@NamedQuery(name = BundleApplications.NQ_FIND_JOIN_SUPPORTED_LANGUAGE_LOCALE, query = "select ba from BundleApplications ba inner join ba.supportedLocales sl where sl.id = :languageLocale") })

@Table(name = BundleApplications.TABLE_NAME, uniqueConstraints = {
		@UniqueConstraint(name = DatabasePrefix.UNIQUE_CONSTRAINT_PREFIX
			+ BundleApplications.TABLE_NAME + DatabasePrefix.UNDERSCORE_PREFIX
			+ NameEntity.COLUMN_NAME_NAME, columnNames = NameEntity.COLUMN_NAME_NAME) }, indexes = {
					@Index(name = DatabasePrefix.INDEX_PREFIX + BundleApplications.TABLE_NAME
						+ DatabasePrefix.UNDERSCORE_PREFIX
						+ NameEntity.COLUMN_NAME_NAME, columnList = NameEntity.COLUMN_NAME_NAME, unique = true) })
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@GenericGenerator(name = BaseEntity.SEQUENCE_GENERIC_GENERATOR_NAME, strategy = IdentifiableSequenceStyleGenerator.STRATEGY_CLASS_NAME, parameters = @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = DatabasePrefix.SEQUENCE_GENERATOR_PREFIX
	+ BundleApplications.TABLE_NAME))
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class BundleApplications extends VersionableNameUUIDEntity implements Cloneable
{

	/** The Constant BASE_BUNDLE_APPLICATION is the base name of the initial bundle application. */
	public static final String BASE_BUNDLE_APPLICATION = "base-bundle-application";
	public static final String NQ_FIND_JOIN_SUPPORTED_LANGUAGE_LOCALE = "BundleApplications."
		+ "findWithJoinSupportedLanguageLocale";

	public static final String NQ_FIND_SUPPORTED_LANGUAGE_LOCALE = "BundleApplications."
		+ "findSupportedLanguageLocale";
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;


	public static final String TABLE_NAME = "bundle_applications";
	@Column
	Integer id;

	/**
	 * The default locale of this bundle application.
	 */
	@ManyToOne(fetch = FetchType.EAGER,	cascade = { CascadeType.MERGE, CascadeType.REFRESH	})
	@JoinColumn(name = "default_locale_id", nullable = true, referencedColumnName = "uuid", foreignKey = @ForeignKey(name = "fk_bundle_applications_default_locale_id"))
	LanguageLocales defaultLocale;

	/**
	 * The supported locale objects that are mandatory for this bundle application.
	 */
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL	})
	@JoinTable(name = "bundle_application_language_locales", joinColumns = {
			@JoinColumn(name = "application_id", referencedColumnName = "uuid", foreignKey = @ForeignKey(name = "fk_bundle_application_id")) }, inverseJoinColumns = {
					@JoinColumn(name = "language_locales_id", referencedColumnName = "uuid", foreignKey = @ForeignKey(name = "fk_bundle_application_language_locales_id")) }) Set<LanguageLocales> supportedLocales = new HashSet<>();

	/**
	 * Adds the given {@link LanguageLocales} to the supported language locales.
	 *
	 * @param supportedLocale
	 *            the supported locale to add
	 * @return true, if successful
	 */
	public boolean addSupportedLanguageLocale(LanguageLocales supportedLocale)
	{
		if (supportedLocales == null)
		{
			supportedLocales = new HashSet<>();
		}
		return supportedLocales.add(supportedLocale);
	}

	/**
	 * Checks if the given {@link LanguageLocales} is supported
	 *
	 * @param supportedLocale
	 *            the supported locale
	 * @return true, if the given {@link LanguageLocales} is supported otherwise false
	 */
	public boolean isSupported(LanguageLocales supportedLocale)
	{
		return getSupportedLocales().contains(supportedLocale);
	}

	/**
	 * Removes the supported language locale.
	 *
	 * @param supportedLocale
	 *            the supported locale
	 * @return true, if successful
	 */
	public boolean removeSupportedLanguageLocale(LanguageLocales supportedLocale)
	{
		if (supportedLocales == null)
		{
			supportedLocales = new HashSet<>();
			return false;
		}
		return supportedLocales.remove(supportedLocale);
	}

}
