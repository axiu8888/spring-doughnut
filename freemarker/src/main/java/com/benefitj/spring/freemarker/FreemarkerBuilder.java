package com.benefitj.spring.freemarker;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import freemarker.cache.*;
import freemarker.core.*;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.MemberAccessPolicy;
import freemarker.template.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FreemarkerBuilder {

  private Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);

  public Configuration getConfiguration() {
    return configuration;
  }

  public FreemarkerBuilder setConfiguration(Configuration configuration) {
    this.configuration = configuration;
    return this;
  }


  /**
   * Sets a {@link TemplateLoader} that is used to look up and load templates;
   * as a side effect the template cache will be emptied (unless the new and the old values are the same).
   * By providing your own {@link TemplateLoader} implementation, you can load templates from whatever kind of
   * storages, like from relational databases, NoSQL-storages, etc.
   *
   * <p>Convenience methods exists to install commonly used loaders, instead of using this method:
   * {@link #setClassForTemplateLoading(Class, String)},
   * {@link #setClassLoaderForTemplateLoading(ClassLoader, String)},
   * {@link #setDirectoryForTemplateLoading(File)}, and
   * {@link #setServletContextForTemplateLoading(Object, String)}.
   *
   * <p>You can chain several {@link TemplateLoader}-s together with {@link MultiTemplateLoader}.
   *
   * <p>Default value: You should always set the template loader instead of relying on the default value.
   * (But if you still care what it is, before "incompatible improvements" 2.3.21 it's a {@link FileTemplateLoader}
   * that uses the current directory as its root; as it's hard tell what that directory will be, it's not very useful
   * and dangerous. Starting with "incompatible improvements" 2.3.21 the default is {@code null}.)
   *
   * @param templateLoader
   */
  public FreemarkerBuilder setTemplateLoader(TemplateLoader templateLoader) {
    getConfiguration().setTemplateLoader(templateLoader);
    return this;
  }

  /**
   * Sets the {@link TemplateLookupStrategy} that is used to look up templates based on the requested name; as a side
   * effect the template cache will be emptied. The default value is {@link TemplateLookupStrategy#DEFAULT_2_3_0}.
   *
   * @param templateLookupStrategy
   * @since 2.3.22
   */
  public FreemarkerBuilder setTemplateLookupStrategy(TemplateLookupStrategy templateLookupStrategy) {
    getConfiguration().setTemplateLookupStrategy(templateLookupStrategy);
    return this;
  }

  /**
   * Sets the template name format used. The default is {@link TemplateNameFormat#DEFAULT_2_3_0}, while the
   * recommended value for new projects is {@link TemplateNameFormat#DEFAULT_2_4_0}.
   *
   * @param templateNameFormat
   * @since 2.3.22
   */
  public FreemarkerBuilder setTemplateNameFormat(TemplateNameFormat templateNameFormat) {
    getConfiguration().setTemplateNameFormat(templateNameFormat);
    return this;
  }

  /**
   * Sets a {@link TemplateConfigurationFactory} that will configure individual templates where their settings differ
   * from those coming from the common {@link Configuration} object. A typical use case for that is specifying the
   * {@link TemplateConfiguration#setOutputFormat(OutputFormat) outputFormat} for templates based on their file
   * extension or parent directory.
   *
   * <p>
   * Note that the settings suggested by standard file extensions are stronger than that you set here. See
   * {@link #setRecognizeStandardFileExtensions(boolean)} for more information about standard file extensions.
   *
   * <p>See "Template configurations" in the FreeMarker Manual for examples.
   *
   * @param templateConfigurations
   * @since 2.3.24
   */
  public FreemarkerBuilder setTemplateConfigurations(TemplateConfigurationFactory templateConfigurations) {
    getConfiguration().setTemplateConfigurations(templateConfigurations);
    return this;
  }

  /**
   * Sets the {@link CacheStorage} used for caching {@link Template}-s;
   * the earlier content of the template cache will be dropt.
   * <p>
   * The default is a {@link SoftCacheStorage}. If the total size of the {@link Template}
   * objects is significant but most templates are used rarely, using a
   * {@link MruCacheStorage} instead might be advisable. If you don't want caching at
   * all, use {@link NullCacheStorage} (you can't use {@code null}).
   *
   * <p>Note that setting the cache storage will re-create the template cache, so
   * all its content will be lost.
   *
   * @param cacheStorage
   */
  public FreemarkerBuilder setCacheStorage(CacheStorage cacheStorage) {
    getConfiguration().setCacheStorage(cacheStorage);
    return this;
  }

  /**
   * Sets the file system directory from which to load templates. This is equivalent to
   * {@code setTemplateLoader(new FileTemplateLoader(dir))}, so see
   * {@link FileTemplateLoader#FileTemplateLoader(File)} for more details.
   *
   * <p>
   * Note that FreeMarker can load templates from non-file-system sources too. See
   * {@link #setTemplateLoader(TemplateLoader)} from more details.
   *
   * <p>
   * Note that this shouldn't be used for loading templates that are coming from a WAR; use
   * {@link #setServletContextForTemplateLoading(Object, String)} then. Servlet containers might not unpack the WAR
   * file, in which case you clearly can't access the contained files via {@link File}. Even if the WAR is unpacked,
   * the servlet container might not expose the location as a {@link File}.
   * {@link #setServletContextForTemplateLoading(Object, String)} on the other hand will work in all these cases.
   *
   * @param dir
   */
  public FreemarkerBuilder setDirectoryForTemplateLoading(File dir) {
    CatchUtils.tryThrow(() -> getConfiguration().setDirectoryForTemplateLoading(dir));
    return this;
  }

  /**
   * Sets the servlet context from which to load templates.
   * This is equivalent to {@code setTemplateLoader(new WebappTemplateLoader(sctxt, path))}
   * or {@code setTemplateLoader(new WebappTemplateLoader(sctxt))} if {@code path} was
   * {@code null}, so see {@code freemarker.cache.WebappTemplateLoader} for more details.
   *
   * @param servletContext the {@code javax.servlet.ServletContext} object. (The declared type is {@link Object}
   *                       to prevent class loading error when using FreeMarker in an environment where
   *                       there's no servlet classes available.)
   * @param path           the path relative to the ServletContext.
   * @see #setTemplateLoader(TemplateLoader)
   */
  public FreemarkerBuilder setServletContextForTemplateLoading(Object servletContext, String path) {
    getConfiguration().setServletContextForTemplateLoading(servletContext, path);
    return this;
  }

  /**
   * Sets the class whose {@link Class#getResource(String)} method will be used to load templates, from the inside the
   * package specified. See {@link ClassTemplateLoader#ClassTemplateLoader(Class, String)} for more details.
   *
   * @param resourceLoaderClass
   * @param basePackagePath     Separate steps with {@code "/"}, not {@code "."}, and note that it matters if this starts with
   *                            {@code /} or not. See {@link ClassTemplateLoader#ClassTemplateLoader(Class, String)} for more details.
   * @see #setClassLoaderForTemplateLoading(ClassLoader, String)
   * @see #setTemplateLoader(TemplateLoader)
   */
  public FreemarkerBuilder setClassForTemplateLoading(Class resourceLoaderClass, String basePackagePath) {
    getConfiguration().setClassForTemplateLoading(resourceLoaderClass, basePackagePath);
    return this;
  }

  /**
   * Sets the {@link ClassLoader} whose {@link ClassLoader#getResource(String)} method will be used to load templates,
   * from the inside the package specified. See {@link ClassTemplateLoader#ClassTemplateLoader(Class, String)} for
   * more details.
   *
   * @param classLoader
   * @param basePackagePath Separate steps with {@code "/"}, not {@code "."}. See
   *                        {@link ClassTemplateLoader#ClassTemplateLoader(Class, String)} for more details.
   * @see #setClassForTemplateLoading(Class, String)
   * @see #setTemplateLoader(TemplateLoader)
   * @since 2.3.22
   */
  public FreemarkerBuilder setClassLoaderForTemplateLoading(ClassLoader classLoader, String basePackagePath) {
    getConfiguration().setClassLoaderForTemplateLoading(classLoader, basePackagePath);
    return this;
  }

//  /**
//   * Sets the time in seconds that must elapse before checking whether there is a newer version of a template "file"
//   * than the cached one.
//   *
//   * <p>
//   * Historical note: Despite what the API documentation said earlier, this method is <em>not</em> thread-safe. While
//   * it works well on most hardware, it's not guaranteed that FreeMarker will see the update in all threads, and
//   * theoretically it's also possible that it will see a value that's a binary mixture of the new and the old one.
//   *
//   * @param seconds
//   * @deprecated Use {@link #setTemplateUpdateDelayMilliseconds(long)} instead, because the time granularity of this method
//   * is often misunderstood to be milliseconds.
//   */
//  public FreemarkerBuilder setTemplateUpdateDelay(int seconds) {
//    getConfiguration().setTemplateUpdateDelay(seconds);
//    return this;
//  }

  /**
   * Sets the time in milliseconds that must elapse before checking whether there is a newer version of a template
   * "file" than the cached one. Defaults to 5000 ms.
   *
   * <p>
   * When you get a template via {@link Configuration#getTemplate(String)} (or some of its overloads). FreeMarker will try to get
   * the template from the template cache. If the template is found, and at least this amount of time was elapsed
   * since the template last modification date was checked, FreeMarker will re-check the last modification date (this
   * could mean I/O), possibly reloading the template and updating the cache as a consequence (can mean even more
   * I/O). The {@link Configuration#getTemplate(String)} (or some of its overloads) call will only return after this all is
   * done, so it will return the fresh template.
   *
   * @param millis
   * @since 2.3.23
   */
  public FreemarkerBuilder setTemplateUpdateDelayMilliseconds(long millis) {
    getConfiguration().setTemplateUpdateDelayMilliseconds(millis);
    return this;
  }

//  /**
//   * Sets whether directives such as {@code if}, {@code else}, etc must be written as {@code #if}, {@code #else}, etc.
//   * Defaults to {@code true}.
//   *
//   * <p>When this is {@code true},
//   * any tag not starting with &lt;# or &lt;/# or &lt;@ or &lt;/@ is considered as plain text
//   * and will go to the output as is. Tag starting with &lt;# or &lt;/# must
//   * be valid FTL tag, or else the template is invalid (i.e. &lt;#noSuchDirective&gt;
//   * is an error).
//   *
//   * @param b
//   * @deprecated Only {@code true} (the default) value will be supported sometimes in the future.
//   */
//  public FreemarkerBuilder setStrictSyntaxMode(boolean b) {
//    getConfiguration().setStrictSyntaxMode(b);
//    return this;
//  }

  public FreemarkerBuilder setObjectWrapper(ObjectWrapper objectWrapper) {
    getConfiguration().setObjectWrapper(objectWrapper);
    return this;
  }

  public FreemarkerBuilder setLocale(Locale locale) {
    getConfiguration().setLocale(locale);
    return this;
  }

  public FreemarkerBuilder setTimeZone(TimeZone timeZone) {
    getConfiguration().setTimeZone(timeZone);
    return this;
  }

  public FreemarkerBuilder setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
    getConfiguration().setTemplateExceptionHandler(templateExceptionHandler);
    return this;
  }

  public FreemarkerBuilder setAttemptExceptionReporter(AttemptExceptionReporter attemptExceptionReporter) {
    getConfiguration().setAttemptExceptionReporter(attemptExceptionReporter);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @param value
   * @since 2.3.22
   */
  public FreemarkerBuilder setLogTemplateExceptions(boolean value) {
    getConfiguration().setLogTemplateExceptions(value);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @param value
   * @since 2.3.27
   */
  public FreemarkerBuilder setWrapUncheckedExceptions(boolean value) {
    getConfiguration().setWrapUncheckedExceptions(value);
    return this;
  }

  /**
   * Use {@link Configuration#Configuration(Version)} instead if possible; see the meaning of the parameter there.
   *
   * <p>Do NOT ever use {@link Configuration#getVersion()} to set the "incompatible improvements". Always use a fixed value, like
   * {@link Configuration#VERSION_2_3_30}. Otherwise your application can break as you upgrade FreeMarker. (As of 2.3.30, doing
   * this will be logged as an error. As of 2.4.0, it will be probably disallowed, by throwing exception.)
   *
   * <p>If the default value of a setting depends on the {@code incompatibleImprovements} and the value of that setting
   * was never set in this {@link Configuration} object through the public API, its value will be set to the default
   * value appropriate for the new {@code incompatibleImprovements}. (This adjustment of a setting value doesn't
   * count as setting that setting, so setting {@code incompatibleImprovements} for multiple times also works as
   * expected.) Note that if the {@code template_loader} have to be changed because of this, the template cache will
   * be emptied.
   *
   * @param incompatibleImprovements
   * @throws IllegalArgumentException If {@code incompatibleImmprovements} refers to a version that wasn't released yet when the currently
   *                                  used FreeMarker version was released, or is less than 2.3.0, or is {@code null}.
   * @since 2.3.20
   */
  public FreemarkerBuilder setIncompatibleImprovements(Version incompatibleImprovements) {
    getConfiguration().setIncompatibleImprovements(incompatibleImprovements);
    return this;
  }

//  /**
//   * @param version
//   * @deprecated Use {@link Configuration#Configuration(Version)}, or
//   * as last chance, {@link #setIncompatibleImprovements(Version)} instead.
//   */
//  public FreemarkerBuilder setIncompatibleEnhancements(String version) {
//    getConfiguration().setIncompatibleEnhancements(version);
//    return this;
//  }

  /**
   * Sets whether the FTL parser will try to remove
   * getConfiguration()fluous white-space around certain FTL tags.
   *
   * @param b
   */
  public FreemarkerBuilder setWhitespaceStripping(boolean b) {
    getConfiguration().setWhitespaceStripping(b);
    return this;
  }

  /**
   * Sets when auto-escaping should be enabled depending on the current {@linkplain OutputFormat output format};
   * default is {@link Configuration#ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY}.
   *
   * <p>Note that the default output format, {@link UndefinedOutputFormat}, is a non-escaping format, so there
   * auto-escaping will be off.
   *
   * <p>Note that the templates can turn auto-escaping on/off locally with directives like
   * {@code <#ftl auto_esc=...>}, {@code <#autoesc>...</#autoesc>}, and {@code <#noautoesc>...</#noautoesc>}, which
   * are ignoring the auto-escaping policy.
   *
   * <p><b>About auto-escaping</b></p>
   *
   * <p>
   * Auto-escaping has significance when a value is printed with <code>${...}</code> (or <code>#{...}</code>). If
   * auto-escaping is on, FreeMarker will assume that the value is plain text (as opposed to markup or some kind of
   * rich text), so it will escape it according the current output format (see {@link #setOutputFormat(OutputFormat)}
   * and {@link TemplateConfiguration#setOutputFormat(OutputFormat)}). If auto-escaping is off, FreeMarker will assume
   * that the string value is already in the output format, so it prints it as is to the output.
   *
   * <p>Further notes on auto-escaping:
   * <ul>
   *   <li>When printing numbers, dates, and other kind of non-string values with <code>${...}</code>, they will be
   *       first converted to string (according the formatting settings and locale), then they are escaped just like
   *       string values.
   *   <li>When printing {@link TemplateMarkupOutputModel}-s, they aren't escaped again (they are already escaped).
   *   <li>Auto-escaping doesn't do anything if the current output format isn't an {@link MarkupOutputFormat}.
   *       That's the case for the default output format, {@link UndefinedOutputFormat}, and also for
   *       {@link PlainTextOutputFormat}.
   *   <li>The output format inside a string literal expression is always {@link PlainTextOutputFormat}
   *       (regardless of the output format of the containing template), which is a non-escaping format. Thus for
   *       example, with <code>&lt;#assign s = "foo${bar}"&gt;</code>, {@code bar} will always get into {@code s}
   *       without escaping, but with <code>&lt;#assign s&gt;foo${bar}&lt;#assign&gt;</code> it may will be escaped.
   * </ul>
   *
   * <p>Note that what you set here is just a default, which can be overridden for individual templates via
   * {@link #setTemplateConfigurations(TemplateConfigurationFactory)}. This setting is also overridden by the standard file
   * extensions; see them at {@link #setRecognizeStandardFileExtensions(boolean)}.
   *
   * @param autoEscapingPolicy One of the {@link Configuration#ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY},
   *                           {@link Configuration#ENABLE_IF_SUPPORTED_AUTO_ESCAPING_POLICY}, and {@link Configuration#DISABLE_AUTO_ESCAPING_POLICY} constants.
   * @see TemplateConfiguration#setAutoEscapingPolicy(int)
   * @see Configuration#setOutputFormat(OutputFormat)
   * @see TemplateConfiguration#setOutputFormat(OutputFormat)
   * @since 2.3.24
   */
  public FreemarkerBuilder setAutoEscapingPolicy(int autoEscapingPolicy) {
    getConfiguration().setAutoEscapingPolicy(autoEscapingPolicy);
    return this;
  }

  /**
   * Sets the default output format. Usually, you should leave this on its default, which is
   * {@link UndefinedOutputFormat#INSTANCE}, and then use standard file extensions like "ftlh" (for HTML) or "ftlx"
   * (for XML) and ensure that {@link #setRecognizeStandardFileExtensions(boolean)} is {@code true} (see more there).
   * Where you can't use the standard extensions, templates still can be associated to output formats with
   * patterns matching their name (their path) using {@link #setTemplateConfigurations(TemplateConfigurationFactory)}.
   * But if all templates will have the same output format, you may use {@link #setOutputFormat(OutputFormat)} after
   * all, to a value like {@link HTMLOutputFormat#INSTANCE}, {@link XMLOutputFormat#INSTANCE}, etc. Also note
   * that templates can specify their own output format like {@code
   * <#ftl output_format="HTML">}, which overrides any configuration settings.
   *
   * <p>
   * The output format is mostly important because of auto-escaping (see {@link #setAutoEscapingPolicy(int)}), but
   * maybe also used by the embedding application to set the HTTP response MIME type, etc.
   *
   * @param outputFormat Not {@code null}; use {@link UndefinedOutputFormat#INSTANCE} instead.
   * @see #setRegisteredCustomOutputFormats(Collection)
   * @see #setTemplateConfigurations(TemplateConfigurationFactory)
   * @see #setRecognizeStandardFileExtensions(boolean)
   * @see #setAutoEscapingPolicy(int)
   * @since 2.3.24
   */
  public FreemarkerBuilder setOutputFormat(OutputFormat outputFormat) {
    getConfiguration().setOutputFormat(outputFormat);
    return this;
  }

  /**
   * Sets the custom output formats that can be referred by their unique name ({@link OutputFormat#getName()}) from
   * templates. Names are also used to look up the {@link OutputFormat} for standard file extensions; see them at
   * {@link #setRecognizeStandardFileExtensions(boolean)}.
   *
   * <p>
   * When there's a clash between a custom output format name and a standard output format name, the custom format
   * will win, thus you can override the meaning of standard output format names. Except, it's not allowed to override
   * {@link UndefinedOutputFormat} and {@link PlainTextOutputFormat}.
   *
   * <p>
   * The default value is an empty collection.
   *
   * @param registeredCustomOutputFormats The collection of the {@link OutputFormat}-s, each must be different and has a unique name (
   *                                      {@link OutputFormat#getName()}) within this collection.
   * @throws IllegalArgumentException When multiple different {@link OutputFormat}-s have the same name in the parameter collection. When
   *                                  the same {@link OutputFormat} object occurs for multiple times in the collection. If an
   *                                  {@link OutputFormat} name is 0 long. If an {@link OutputFormat} name doesn't start with letter or
   *                                  digit. If an {@link OutputFormat} name contains {@code '+'} or <code>'{'</code> or <code>'}'</code>.
   *                                  If an {@link OutputFormat} name equals to {@link UndefinedOutputFormat#getName()} or
   *                                  {@link PlainTextOutputFormat#getName()}.
   * @since 2.3.24
   */
  public FreemarkerBuilder setRegisteredCustomOutputFormats(Collection<? extends OutputFormat> registeredCustomOutputFormats) {
    getConfiguration().setRegisteredCustomOutputFormats(registeredCustomOutputFormats);
    return this;
  }

  /**
   * Sets if the "file" extension part of the source name ({@link Template#getSourceName()}) will influence certain
   * parsing settings. For backward compatibility, it defaults to {@code false} if
   * {@link Configuration#getIncompatibleImprovements()} is less than 2.3.24. Starting from {@code incompatibleImprovements}
   * 2.3.24, it defaults to {@code true}, so the following standard file extensions take their effect:
   *
   * <ul>
   *   <li>{@code ftlh}: Sets {@link TemplateConfiguration#setOutputFormat(OutputFormat) outputFormat} to
   *       {@code "HTML"} (i.e., {@link HTMLOutputFormat#INSTANCE}, unless the {@code "HTML"} name is overridden by
   *       {@link #setRegisteredCustomOutputFormats(Collection)}) and
   *       {@link TemplateConfiguration#setAutoEscapingPolicy(int) autoEscapingPolicy} to
   *       {@link Configuration#ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY}.
   *   <li>{@code ftlx}: Sets {@link TemplateConfiguration#setOutputFormat(OutputFormat) outputFormat} to
   *       {@code "XML"} (i.e., {@link XMLOutputFormat#INSTANCE}, unless the {@code "XML"} name is overridden by
   *       {@link #setRegisteredCustomOutputFormats(Collection)}) and
   *       {@link TemplateConfiguration#setAutoEscapingPolicy(int) autoEscapingPolicy} to
   *       {@link Configuration#ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY}.
   * </ul>
   *
   * <p>These file extensions are not case sensitive. The file extension is the part after the last dot in the source
   * name. If the source name contains no dot, then it has no file extension.
   *
   * <p>The settings activated by these file extensions override the setting values dictated by
   * {@link #setTemplateConfigurations(TemplateConfigurationFactory)}.
   *
   * @param recognizeStandardFileExtensions
   */
  public FreemarkerBuilder setRecognizeStandardFileExtensions(boolean recognizeStandardFileExtensions) {
    getConfiguration().setRecognizeStandardFileExtensions(recognizeStandardFileExtensions);
    return this;
  }

  /**
   * Determines the tag syntax (like {@code <#if x>} VS {@code [#if x]}) of the template files
   * that has no {@code #ftl} header to decide that. Don't confuse this with the interpolation syntax
   * ({@link #setInterpolationSyntax(int)}); they are independent.
   *
   * <p>The {@code tagSyntax} parameter must be one of:
   * <ul>
   *   <li>{@link Configuration#AUTO_DETECT_TAG_SYNTAX}:
   *     Use the syntax of the first FreeMarker tag (can be anything, like <tt>#list</tt>,
   *     <tt>#include</tt>, user defined, etc.)
   *   <li>{@link Configuration#ANGLE_BRACKET_TAG_SYNTAX}:
   *     Use the angle bracket tag syntax (the normal syntax), like {@code <#include ...>}
   *   <li>{@link Configuration#SQUARE_BRACKET_TAG_SYNTAX}:
   *     Use the square bracket tag syntax, like {@code [#include ...]}. Note that this does <em>not</em> change
   *     <code>${x}</code> to {@code [=...]}; that's <em>interpolation</em> syntax, so use
   *     {@link #setInterpolationSyntax(int)} for that.
   * </ul>
   *
   * <p>In FreeMarker 2.3.x {@link Configuration#ANGLE_BRACKET_TAG_SYNTAX} is the
   * default for better backward compatibility. Starting from 2.4.x {@link
   * Configuration#AUTO_DETECT_TAG_SYNTAX} is the default, so it's recommended to use
   * that even for 2.3.x.
   *
   * <p>This setting is ignored for the templates that have {@code ftl} directive in
   * it. For those templates the syntax used for the {@code ftl} directive determines
   * the syntax.
   *
   * @param tagSyntax
   * @see #setInterpolationSyntax(int)
   */
  public FreemarkerBuilder setTagSyntax(int tagSyntax) {
    getConfiguration().setTagSyntax(tagSyntax);
    return this;
  }

  /**
   * Determines the interpolation syntax (like <code>${x}</code> VS <code>[=x]</code>) of the template files. Don't
   * confuse this with the tag syntax ({@link #setTagSyntax(int)}); they are independent.
   *
   * <p>
   * The {@code interpolationSyntax} parameter must be one of {@link Configuration#LEGACY_INTERPOLATION_SYNTAX},
   * {@link Configuration#DOLLAR_INTERPOLATION_SYNTAX}, and {@link Configuration#SQUARE_BRACKET_INTERPOLATION_SYNTAX}.
   * Note that {@link Configuration#SQUARE_BRACKET_INTERPOLATION_SYNTAX} does <em>not</em> change {@code <#if x>} to
   * {@code [#if x]}; that's <em>tag</em> syntax, so use {@link #setTagSyntax(int)} for that.
   *
   * @param interpolationSyntax
   * @see #setTagSyntax(int)
   * @since 2.3.28
   */
  public FreemarkerBuilder setInterpolationSyntax(int interpolationSyntax) {
    getConfiguration().setInterpolationSyntax(interpolationSyntax);
    return this;
  }

  /**
   * Sets the naming convention used for the identifiers that are part of the template language. The available naming
   * conventions are legacy (directive (tag) names are all-lower-case {@code likethis}, others are snake case
   * {@code like_this}), and camel case ({@code likeThis}). The default is auto-detect, which detects the naming
   * convention used and enforces that same naming convention for the whole template.
   *
   * <p>
   * This setting doesn't influence what naming convention is used for the setting names outside templates. Also, it
   * won't ever convert the names of user-defined things, like of data-model members, or the names of user defined
   * macros/functions. It only influences the names of the built-in directives ({@code #elseIf} VS {@code elseif}),
   * built-ins ({@code ?upper_case} VS {@code ?upperCase} ), special variables ({@code .data_model} VS
   * {@code .dataModel}).
   *
   * <p>
   * Which convention to use: FreeMarker prior to 2.3.23 has only supported
   * {@link Configuration#LEGACY_NAMING_CONVENTION}, so that's how most templates and examples out there are written
   * as of 2015. But as templates today are mostly written by programmers and often access Java API-s which already
   * use camel case, {@link Configuration#CAMEL_CASE_NAMING_CONVENTION} is the recommended option for most projects.
   * However, it's no necessary to make a application-wide decision; see auto-detection below.
   *
   * <p>
   * FreeMarker will decide the naming convention automatically for each template individually when this setting is
   * set to {@link Configuration#AUTO_DETECT_NAMING_CONVENTION} (which is the default). The naming convention of a template is
   * decided when the first core (non-user-defined) identifier is met during parsing (not during processing) where the
   * naming convention is relevant (like for {@code s?upperCase} or {@code s?upper_case} it's relevant, but for
   * {@code s?length} it isn't). At that point, the naming convention of the template is decided, and any later core
   * identifier that uses a different convention will be a parsing error. As the naming convention is decided per
   * template, it's not a problem if a template and the other template it {@code #include}-s/{@code #import} uses a
   * different convention.
   *
   * <p>
   * FreeMarker always enforces the same naming convention to be used consistently within the same template "file".
   * Additionally, when this setting is set to non-{@link Configuration#AUTO_DETECT_NAMING_CONVENTION}, the selected naming
   * convention is enforced on all templates. Thus such a setup can be used to enforce an application-wide naming
   * convention.
   *
   * <p>
   * Non-strict tags (a long deprecated syntax from FreeMarker 1, activated via {@link #setStrictSyntaxMode(boolean)})
   * are only recognized as FTL tags when they are using the {@link Configuration#LEGACY_NAMING_CONVENTION} syntax,
   * regardless of this setting. As they aren't exempt from the naming convention consistency enforcement, generally,
   * you can't use strict {@link Configuration#CAMEL_CASE_NAMING_CONVENTION} tags mixed with non-strict tags.
   *
   * @param namingConvention One of the {@link Configuration#AUTO_DETECT_NAMING_CONVENTION} or {@link Configuration#LEGACY_NAMING_CONVENTION}
   *                         {@link Configuration#CAMEL_CASE_NAMING_CONVENTION}.
   * @throws IllegalArgumentException If the parameter isn't one of the valid constants.
   * @since 2.3.23
   */
  public FreemarkerBuilder setNamingConvention(int namingConvention) {
    getConfiguration().setNamingConvention(namingConvention);
    return this;
  }

  /**
   * Sets the assumed display width of the tab character (ASCII 9), which influences the column number shown in error
   * messages (or the column number you get through other API-s). So for example if the users edit templates in an
   * editor where the tab width is set to 4, you should set this to 4 so that the column numbers printed by FreeMarker
   * will match the column number shown in the editor. This setting doesn't affect the output of templates, as a tab
   * in the template will remain a tab in the output too. If you set this setting to 1, then tab characters will be
   * kept in the return value of {@link Template#getSource(int, int, int, int)}, otherwise they will be replaced with
   * the appropriate number of spaces.
   *
   * @param tabSize At least 1, at most 256.
   * @since 2.3.25
   */
  public FreemarkerBuilder setTabSize(int tabSize) {
    getConfiguration().setTabSize(tabSize);
    return this;
  }

  /**
   * Specifies the behavior when reading a loop variable (like {@code i} in {@code <#list items as i>}, or in
   * {@code <@myMacro items; i>}) that's {@code null} (missing); if {@code true}, FreeMarker will look for a variable
   * with the same name in higher variable scopes, or if {@code false} the variable will be simply {@code null}
   * (missing). For backward compatibility the default is {@code true}. The recommended value for new projects is
   * {@code false}, as otherwise adding new variables to higher scopes (typically to the data-model) can
   * unintentionally change the behavior of templates. You have to be quite unlucky for that to happen though:
   * The newly added variable has to have the same name as the loop variable, and there must be some null (missing)
   * values in what you loop through.
   *
   * <p>This setting doesn't influence the behavior of lambdas, like {@code items?filter(i -> i?hasContent)}, as they
   * never had this problem. Reading a lambda argument never falls back to higher scopes.
   *
   * @param fallbackOnNullLoopVariable
   * @since 2.3.29
   */
  public FreemarkerBuilder setFallbackOnNullLoopVariable(boolean fallbackOnNullLoopVariable) {
    getConfiguration().setFallbackOnNullLoopVariable(fallbackOnNullLoopVariable);
    return this;
  }

  /**
   * Sets the charset used for decoding byte sequences to character sequences when
   * reading template files in a locale for which no explicit encoding
   * was specified via {@link #setEncoding(Locale, String)}. Note that by default there is no locale specified for
   * any locale, so the default encoding is always in effect.
   *
   * <p>Defaults to the default system encoding, which can change from one server to
   * another, so <b>you should always set this setting</b>. If you don't know what charset your should chose,
   * {@code "UTF-8"} is usually a good choice.
   *
   * <p>Note that individual templates may specify their own charset by starting with
   * <tt>&lt;#ftl encoding="..."&gt;</tt>
   *
   * @param encoding The name of the charset, such as {@code "UTF-8"} or {@code "ISO-8859-1"}
   */
  public FreemarkerBuilder setDefaultEncoding(String encoding) {
    getConfiguration().setDefaultEncoding(encoding);
    return this;
  }

  /**
   * Sets the character set encoding to use for templates of
   * a given locale. If there is no explicit encoding set for some
   * locale, then the default encoding will be used, what you can
   * set with {@link #setDefaultEncoding}.
   *
   * @param locale
   * @param encoding
   * @see Configuration#clearEncodingMap
   * @see Configuration#loadBuiltInEncodingMap
   */
  public FreemarkerBuilder setEncoding(Locale locale, String encoding) {
    getConfiguration().setEncoding(locale, encoding);
    return this;
  }

  /**
   * Adds a shared variable to the configuration.
   * Shared sharedVariables are sharedVariables that are visible
   * as top-level sharedVariables for all templates which use this
   * configuration, if the data model does not contain a
   * variable with the same name.
   *
   * <p>Never use <tt>TemplateModel</tt> implementation that is not thread-safe for shared sharedVariables,
   * if the configuration is used by multiple threads! It is the typical situation for Servlet based Web sites.
   *
   * <p>This method is <b>not</b> thread safe; use it with the same restrictions as those that modify setting values.
   *
   * @param name the name used to access the data object from your template.
   *             If a shared variable with this name already exists, it will replace
   *             that.
   * @param tm
   * @see #setAllSharedVariables
   * @see #setSharedVariable(String, Object)
   */
  public FreemarkerBuilder setSharedVariable(String name, TemplateModel tm) {
    getConfiguration().setSharedVariable(name, tm);
    return this;
  }

  /**
   * Adds shared variable to the configuration; It uses {@link Configurable#getObjectWrapper()} to wrap the
   * {@code value}, so it's important that the object wrapper is set before this.
   *
   * <p>This method is <b>not</b> thread safe; use it with the same restrictions as those that modify setting values.
   *
   * <p>The added value should be thread safe, if you are running templates from multiple threads with this
   * configuration.
   *
   * @param name
   * @param value
   * @throws TemplateModelException If some of the variables couldn't be wrapped via {@link Configuration#getObjectWrapper()}.
   * @see #setSharedVaribles(Map)
   * @see #setSharedVariable(String, TemplateModel)
   * @see #setAllSharedVariables(TemplateHashModelEx)
   */
  public FreemarkerBuilder setSharedVariable(String name, Object value) {
    CatchUtils.tryThrow(() -> getConfiguration().setSharedVariable(name, value));
    return this;
  }

  /**
   * Replaces all shared variables (removes all previously added ones).
   *
   * <p>The values in the map can be {@link TemplateModel}-s or plain Java objects which will be immediately converted
   * to {@link TemplateModel} with the {@link ObjectWrapper} returned by {@link Configuration#getObjectWrapper()}. If
   * {@link #setObjectWrapper(ObjectWrapper)} is called later, this conversion will be re-applied. Thus, ignoring some
   * extra resource usage, it doesn't mater if in what order are {@link #setObjectWrapper(ObjectWrapper)} and
   * {@link #setSharedVaribles(Map)} called. This is essential when you don't have control over the order in which
   * the setters are called.
   *
   * <p>The values in the map must be thread safe, if you are running templates from multiple threads with
   * this configuration. This means that both the plain Java object and the {@link TemplateModel}-s created from them
   * by the {@link ObjectWrapper} must be thread safe. (The standard {@link ObjectWrapper}-s of FreeMarker create
   * thread safe {@link TemplateModel}-s.) The {@link Map} itself need not be thread-safe.
   *
   * <p>This setter method has no getter pair because of the tricky relation ship with
   * {@link #setSharedVariable(String, Object)}.
   *
   * @param map
   * @throws TemplateModelException If some of the variables couldn't be wrapped via {@link Configuration#getObjectWrapper()}.
   * @since 2.3.29
   */
  public FreemarkerBuilder setSharedVariables(Map<String, ?> map) {
    CatchUtils.tryThrow(() -> getConfiguration().setSharedVariables(map));
    return this;
  }

//  /**
//   * Same as {@link #setSharedVariables(Map)}, but with typo in the name.
//   *
//   * @param map
//   * @since 2.3.21
//   * @deprecated Use {@link #setSharedVariables(Map)} instead.
//   */
//  public FreemarkerBuilder setSharedVaribles(Map map) {
//    CatchUtils.tryThrow(() -> getConfiguration().setSharedVaribles(map));
//    return this;
//  }

  /**
   * Adds all object in the hash as shared variable to the configuration; it's like doing several
   * {@link #setSharedVariable(String, Object)} calls, one for each hash entry. It doesn't remove the already added
   * shared variable before doing this.
   *
   * <p>Never use <tt>TemplateModel</tt> implementation that is not thread-safe for shared shared variable values,
   * if the configuration is used by multiple threads! It is the typical situation for Servlet based Web sites.
   *
   * <p>This method is <b>not</b> thread safe; use it with the same restrictions as those that modify setting values.
   *
   * @param hash a hash model whose objects will be copied to the
   *             configuration with same names as they are given in the hash.
   *             If a shared variable with these names already exist, it will be replaced
   *             with those from the map.
   * @see #setSharedVaribles(Map)
   * @see #setSharedVariable(String, Object)
   * @see #setSharedVariable(String, TemplateModel)
   */
  public FreemarkerBuilder setAllSharedVariables(TemplateHashModelEx hash) {
    CatchUtils.tryThrow(() -> getConfiguration().setAllSharedVariables(hash));
    return this;
  }

  /**
   * Enables/disables localized template lookup. Enabled by default.
   *
   * <p>
   * With the default {@link TemplateLookupStrategy}, localized lookup works like this: Let's say your locale setting
   * is {@code Locale("en", "AU")}, and you call {@link Configuration#getTemplate(String) cfg.getTemplate("foo.ftl")}.
   * Then FreeMarker will look for the template under these names, stopping at the first that exists:
   * {@code "foo_en_AU.ftl"}, {@code "foo_en.ftl"}, {@code "foo.ftl"}. See the description of the default value at
   * {@link #setTemplateLookupStrategy(TemplateLookupStrategy)} for a more details. If you need to generate different
   * template names, use {@link #setTemplateLookupStrategy(TemplateLookupStrategy)} with your custom
   * {@link TemplateLookupStrategy}.
   *
   * <p>Note that changing the value of this setting causes the template cache to be emptied so that old lookup
   * results won't be reused (since 2.3.22).
   *
   * <p>
   * Historical note: Despite what the API documentation said earlier, this method is <em>not</em> thread-safe. While
   * setting it can't cause any serious problems, and in fact it works well on most hardware, it's not guaranteed that
   * FreeMarker will see the update in all threads.
   *
   * @param localizedLookup
   */
  public FreemarkerBuilder setLocalizedLookup(boolean localizedLookup) {
    getConfiguration().setLocalizedLookup(localizedLookup);
    return this;
  }

  public FreemarkerBuilder setSetting(String name, String value) {
    CatchUtils.tryThrow(() -> getConfiguration().setSetting(name, value));
    return this;
  }

  /**
   * Toggles the "Classic Compatible" mode. For a comprehensive description
   * of this mode, see {@link Configuration#isClassicCompatible()}.
   *
   * @param classicCompatibility
   */
  public FreemarkerBuilder setClassicCompatible(boolean classicCompatibility) {
    getConfiguration().setClassicCompatible(classicCompatibility);
    return this;
  }

  /**
   * Same as {@link #setClassicCompatible(boolean)}, but allows some extra values.
   *
   * @param classicCompatibility {@code 0} means {@code false}, {@code 1} means {@code true},
   *                             {@code 2} means {@code true} but with emulating bugs in early 2.x classic-compatibility mode. Currently
   *                             {@code 2} affects how booleans are converted to string; with {@code 1} it's always {@code "true"}/{@code ""},
   *                             but with {@code 2} it's {@code "true"}/{@code "false"} for values wrapped by {@link BeansWrapper} as then
   *                             {@link Boolean#toString()} prevails. Note that {@code someBoolean?string} will always consistently format the
   *                             boolean according the {@code boolean_format} setting, just like in FreeMarker 2.3 and later.
   */
  public FreemarkerBuilder setClassicCompatibleAsInt(int classicCompatibility) {
    getConfiguration().setClassicCompatibleAsInt(classicCompatibility);
    return this;
  }

  /**
   * Sets the time zone used when dealing with {@link Date java.sql.Date} and
   * {@link Time java.sql.Time} values. It defaults to {@code null} for backward compatibility, but in most
   * applications this should be set to the JVM default time zone (server default time zone), because that's what
   * most JDBC drivers will use when constructing the {@link Date java.sql.Date} and
   * {@link Time java.sql.Time} values. If this setting is {@code null}, FreeMarker will use the value of
   * ({@link Configuration#getTimeZone()}) for {@link Date java.sql.Date} and {@link Time java.sql.Time} values,
   * which often gives bad results.
   *
   * <p>This setting doesn't influence the formatting of other kind of values (like of
   * {@link Timestamp java.sql.Timestamp} or plain {@link java.util.Date java.util.Date} values).
   *
   * <p>To decide what value you need, a few things has to be understood:
   * <ul>
   *   <li>Date-only and time-only values in SQL-oriented databases usually store calendar and clock field
   *   values directly (year, month, day, or hour, minute, seconds (with decimals)), as opposed to a set of points
   *   on the physical time line. Thus, unlike SQL timestamps, these values usually aren't meant to be shown
   *   differently depending on the time zone of the audience.
   *
   *   <li>When a JDBC query has to return a date-only or time-only value, it has to convert it to a point on the
   *   physical time line, because that's what {@link java.util.Date} and its subclasses store (milliseconds since
   *   the epoch). Obviously, this is impossible to do. So JDBC just chooses a physical time which, when rendered
   *   <em>with the JVM default time zone</em>, will give the same field values as those stored
   *   in the database. (Actually, you can give JDBC a calendar, and so it can use other time zones too, but most
   *   application won't care using those overloads.) For example, assume that the system time zone is GMT+02:00.
   *   Then, 2014-07-12 in the database will be translated to physical time 2014-07-11 22:00:00 UTC, because that
   *   rendered in GMT+02:00 gives 2014-07-12 00:00:00. Similarly, 11:57:00 in the database will be translated to
   *   physical time 1970-01-01 09:57:00 UTC. Thus, the physical time stored in the returned value depends on the
   *   default system time zone of the JDBC client, not just on the content of the database. (This used to be the
   *   default behavior of ORM-s, like Hibernate, too.)
   *
   *   <li>The value of the {@code time_zone} FreeMarker configuration setting sets the time zone used for the
   *   template output. For example, when a web page visitor has a preferred time zone, the web application framework
   *   may calls {@link Environment#setTimeZone(TimeZone)} with that time zone. Thus, the visitor will
   *   see {@link Timestamp java.sql.Timestamp} and plain {@link java.util.Date java.util.Date} values as
   *   they look in his own time zone. While
   *   this is desirable for those types, as they meant to represent physical points on the time line, this is not
   *   necessarily desirable for date-only and time-only values. When {@code sql_date_and_time_time_zone} is
   *   {@code null}, {@code time_zone} is used for rendering all kind of date/time/dateTime values, including
   *   {@link Date java.sql.Date} and {@link Time java.sql.Time}, and then if, for example,
   *   {@code time_zone} is GMT+00:00, the
   *   values from the earlier examples will be shown as 2014-07-11 (one day off) and 09:57:00 (2 hours off). While
   *   those are the time zone correct renderings, those values are probably meant to be shown "as is".
   *
   *   <li>You may wonder why this setting isn't simply "SQL time zone", that is, why's this time zone not applied to
   *   {@link Timestamp java.sql.Timestamp} values as well. Timestamps in databases refer to a point on
   *   the physical time line, and thus doesn't have the inherent problem of date-only and time-only values.
   *   FreeMarker assumes that the JDBC driver converts time stamps coming from the database so that they store
   *   the distance from the epoch (1970-01-01 00:00:00 UTC), as requested by the {@link java.util.Date} API.
   *   Then time stamps can be safely rendered in different time zones, and thus need no special treatment.
   * </ul>
   *
   * @param tz Maybe {@code null}, in which case {@link Date java.sql.Date} and
   *           {@link Time java.sql.Time} values will be formatted in the time zone returned by
   *           {@link Configuration#getTimeZone()}.
   *           (Note that since {@code null} is an allowed value for this setting, it will not cause
   *           {@link Configuration#getSQLDateAndTimeTimeZone()} to fall back to the parent configuration.)
   * @see #setTimeZone(TimeZone)
   * @since 2.3.21
   */
  public FreemarkerBuilder setSQLDateAndTimeTimeZone(TimeZone tz) {
    getConfiguration().setSQLDateAndTimeTimeZone(tz);
    return this;
  }

  /**
   * Sets the default number format used to convert numbers to strings. Currently, this is one of these:
   * <ul>
   *   <li>{@code "number"}: The number format returned by {@link NumberFormat#getNumberInstance(Locale)}</li>
   *   <li>{@code "currency"}: The number format returned by {@link NumberFormat#getCurrencyInstance(Locale)}</li>
   *   <li>{@code "percent"}: The number format returned by {@link NumberFormat#getPercentInstance(Locale)}</li>
   *   <li>{@code "computer"}: The number format used by FTL's {@code c} built-in (like in {@code someNumber?c}).</li>
   *   <li>{@link DecimalFormat} pattern (like {@code "0.##"}). This syntax is extended by FreeMarker
   *       so that you can specify options like the rounding mode and the symbols used after a 2nd semicolon. For
   *       example, {@code ",000;; roundingMode=halfUp groupingSeparator=_"} will format numbers like {@code ",000"}
   *       would, but with half-up rounding mode, and {@code _} as the group separator. See more about "extended Java
   *       decimal format" in the FreeMarker Manual.
   *       </li>
   *   <li>If the string starts with {@code @} character followed by a letter then it's interpreted as a custom number
   *       format, but only if either {@link Configuration#getIncompatibleImprovements()} is at least 2.3.24, or
   *       there's any custom formats defined (even if custom date/time/dateTime format). The format of a such string
   *       is <code>"@<i>name</i>"</code> or <code>"@<i>name</i> <i>parameters</i>"</code>, where
   *       <code><i>name</i></code> is the key in the {@link Map} set by {@link #setCustomNumberFormats(Map)}, and
   *       <code><i>parameters</i></code> is parsed by the custom {@link TemplateNumberFormat}.
   *   </li>
   * </ul>
   *
   *
   * <p>Defaults to <tt>"number"</tt>.
   *
   * @param numberFormat
   */
  public FreemarkerBuilder setNumberFormat(String numberFormat) {
    getConfiguration().setNumberFormat(numberFormat);
    return this;
  }

  /**
   * Associates names with formatter factories, which then can be referred by the {@link #setNumberFormat(String)
   * number_format} setting with values starting with <code>@<i>name</i></code>. Beware, if you specify any custom
   * formats here, an initial {@code @} followed by a letter will have special meaning in number/date/time/datetime
   * format strings, even if {@link Configuration#getIncompatibleImprovements() incompatible_improvements} is less
   * than 2.3.24 (starting with {@link Configuration#getIncompatibleImprovements() incompatible_improvements} 2.3.24
   * {@code @} always has special meaning).
   *
   * @param customNumberFormats Can't be {@code null}. The name must start with an UNICODE letter, and can only contain UNICODE
   *                            letters and digits (not {@code _}).
   * @since 2.3.24
   */
  public FreemarkerBuilder setCustomNumberFormats(Map<String, ? extends TemplateNumberFormatFactory> customNumberFormats) {
    getConfiguration().setCustomNumberFormats(customNumberFormats);
    return this;
  }

  /**
   * The string value for the boolean {@code true} and {@code false} values, usually intended for human consumption
   * (not for a computer language), separated with comma. For example, {@code "yes,no"}. Note that white-space is
   * significant, so {@code "yes, no"} is WRONG (unless you want that leading space before "no"). Because the proper
   * way of formatting booleans depends on the context too much, it's probably the best to leave this setting on its
   * default, which will enforce explicit formatting, like <code>${aBoolean?string('on', 'off')}</code>.
   *
   * <p>For backward compatibility the default is {@code "true,false"}, but using that value is denied for automatic
   * boolean-to-string conversion, like <code>${myBoolean}</code> will fail with it. If you generate the piece of
   * output for "computer audience" as opposed to "human audience", then you should write
   * <code>${myBoolean?c}</code>, which will print {@code true} or {@code false}. If you really want to always
   * format for computer audience, then it's might be reasonable to set this setting to {@code c}.
   *
   * <p>Note that automatic boolean-to-string conversion only exists since FreeMarker 2.3.20. Earlier this setting
   * only influenced the result of {@code myBool?string}.
   *
   * @param booleanFormat
   */
  public FreemarkerBuilder setBooleanFormat(String booleanFormat) {
    getConfiguration().setBooleanFormat(booleanFormat);
    return this;
  }

  /**
   * Sets the format used to convert {@link java.util.Date}-s that are time (no date part) values to string-s, also
   * the format that {@code someString?time} will use to parse strings.
   *
   * <p>For the possible values see {@link #setDateTimeFormat(String)}.
   *
   * <p>Defaults to {@code ""}, which is equivalent to {@code "medium"}.
   *
   * @param timeFormat
   */
  public FreemarkerBuilder setTimeFormat(String timeFormat) {
    getConfiguration().setTimeFormat(timeFormat);
    return this;
  }

  /**
   * Sets the format used to convert {@link java.util.Date}-s that are date-only (no time part) values to string-s,
   * also the format that {@code someString?date} will use to parse strings.
   *
   * <p>For the possible values see {@link #setDateTimeFormat(String)}.
   *
   * <p>Defaults to {@code ""} which is equivalent to {@code "medium"}.
   *
   * @param dateFormat
   */
  public FreemarkerBuilder setDateFormat(String dateFormat) {
    getConfiguration().setDateFormat(dateFormat);
    return this;
  }

  /**
   * Sets the format used to convert {@link java.util.Date}-s that are date-time (timestamp) values to string-s,
   * also the format that {@code someString?datetime} will use to parse strings.
   *
   * <p>The possible setting values are (the quotation marks aren't part of the value itself):
   *
   * <ul>
   *   <li><p>Patterns accepted by Java's {@link SimpleDateFormat}, for example {@code "dd.MM.yyyy HH:mm:ss"} (where
   *       {@code HH} means 24 hours format) or {@code "MM/dd/yyyy hh:mm:ss a"} (where {@code a} prints AM or PM, if
   *       the current language is English).
   *
   *   <li><p>{@code "xs"} for XML Schema format, or {@code "iso"} for ISO 8601:2004 format.
   *       These formats allow various additional options, separated with space, like in
   *       {@code "iso m nz"} (or with {@code _}, like in {@code "iso_m_nz"}; this is useful in a case like
   *       {@code lastModified?string.iso_m_nz}). The options and their meanings are:
   *
   *       <ul>
   *         <li><p>Accuracy options:<br>
   *             {@code ms} = Milliseconds, always shown with all 3 digits, even if it's all 0-s.
   *                     Example: {@code 13:45:05.800}<br>
   *             {@code s} = Seconds (fraction seconds are dropped even if non-0), like {@code 13:45:05}<br>
   *             {@code m} = Minutes, like {@code 13:45}. This isn't allowed for "xs".<br>
   *             {@code h} = Hours, like {@code 13}. This isn't allowed for "xs".<br>
   *             Neither = Up to millisecond accuracy, but trailing millisecond 0-s are removed, also the whole
   *                     milliseconds part if it would be 0 otherwise. Example: {@code 13:45:05.8}
   *
   *         <li><p>Time zone offset visibility options:<br>
   *             {@code fz} = "Force Zone", always show time zone offset (even for for
   *                     {@link Date java.sql.Date} and {@link Time java.sql.Time} values).
   *                     But, because ISO 8601 doesn't allow for dates (means date without time of the day) to
   *                     show the zone offset, this option will have no effect in the case of {@code "iso"} with
   *                     dates.<br>
   *             {@code nz} = "No Zone", never show time zone offset<br>
   *             Neither = always show time zone offset, except for {@link Date java.sql.Date}
   *                     and {@link Time java.sql.Time}, and for {@code "iso"} date values.
   *
   *         <li><p>Time zone options:<br>
   *             {@code u} = Use UTC instead of what the {@code time_zone} setting suggests. However,
   *                     {@link Date java.sql.Date} and {@link Time java.sql.Time} aren't affected
   *                     by this (see {@link #setSQLDateAndTimeTimeZone(TimeZone)} to understand why)<br>
   *             {@code fu} = "Force UTC", that is, use UTC instead of what the {@code time_zone} or the
   *                     {@code sql_date_and_time_time_zone} setting suggests. This also effects
   *                     {@link Date java.sql.Date} and {@link Time java.sql.Time} values<br>
   *             Neither = Use the time zone suggested by the {@code time_zone} or the
   *                     {@code sql_date_and_time_time_zone} configuration setting ({@link #setTimeZone(TimeZone)} and
   *                     {@link #setSQLDateAndTimeTimeZone(TimeZone)}).
   *       </ul>
   *
   *       <p>The options can be specified in any order.</p>
   *
   *       <p>Options from the same category are mutually exclusive, like using {@code m} and {@code s}
   *       together is an error.
   *
   *       <p>The accuracy and time zone offset visibility options don't influence parsing, only formatting.
   *       For example, even if you use "iso m nz", "2012-01-01T15:30:05.125+01" will be parsed successfully and with
   *       milliseconds accuracy.
   *       The time zone options (like "u") influence what time zone is chosen only when parsing a string that doesn't
   *       contain time zone offset.
   *
   *       <p>Parsing with {@code "iso"} understands both extend format and basic format, like
   *       {@code 20141225T235018}. It doesn't, however, support the parsing of all kind of ISO 8601 strings: if
   *       there's a date part, it must use year, month and day of the month values (not week of the year), and the
   *       day can't be omitted.
   *
   *       <p>The output of {@code "iso"} is deliberately so that it's also a good representation of the value with
   *       XML Schema format, except for 0 and negative years, where it's impossible. Also note that the time zone
   *       offset is omitted for date values in the {@code "iso"} format, while it's preserved for the {@code "xs"}
   *       format.
   *
   *   <li><p>{@code "short"}, {@code "medium"}, {@code "long"}, or {@code "full"}, which that has locale-dependent
   *       meaning defined by the Java platform (see in the documentation of {@link DateFormat}).
   *       For date-time values, you can specify the length of the date and time part independently, be separating
   *       them with {@code _}, like {@code "short_medium"}. ({@code "medium"} means
   *       {@code "medium_medium"} for date-time values.)
   *
   *   <li><p>Anything that starts with {@code "@"} followed by a letter is interpreted as a custom
   *       date/time/dateTime format, but only if either {@link Configuration#getIncompatibleImprovements()}
   *       is at least 2.3.24, or there's any custom formats defined (even if custom number format). The format of
   *       such string is <code>"@<i>name</i>"</code> or <code>"@<i>name</i> <i>parameters</i>"</code>, where
   *       <code><i>name</i></code> is the key in the {@link Map} set by {@link #setCustomDateFormats(Map)}, and
   *       <code><i>parameters</i></code> is parsed by the custom number format.
   *
   * </ul>
   *
   * <p>Defaults to {@code ""}, which is equivalent to {@code "medium_medium"}.
   *
   * @param dateTimeFormat
   */
  public FreemarkerBuilder setDateTimeFormat(String dateTimeFormat) {
    getConfiguration().setDateTimeFormat(dateTimeFormat);
    return this;
  }

  /**
   * Associates names with formatter factories, which then can be referred by the {@link #setDateTimeFormat(String)
   * date_format}, {@link #setDateTimeFormat(String) time_format}, and {@link #setDateTimeFormat(String)
   * datetime_format} settings with values starting with <code>@<i>name</i></code>. Beware, if you specify any custom
   * formats here, an initial {@code @} followed by a letter will have special meaning in number/date/time/datetime
   * format strings, even if {@link Configuration#getIncompatibleImprovements() incompatible_improvements} is less
   * than 2.3.24 (starting with {@link Configuration#getIncompatibleImprovements() incompatible_improvements} 2.3.24
   * {@code @} always has special meaning).
   *
   * @param customDateFormats Can't be {@code null}. The name must start with an UNICODE letter, and can only contain UNICODE
   *                          letters and digits.
   * @since 2.3.24
   */
  public FreemarkerBuilder setCustomDateFormats(Map<String, ? extends TemplateDateFormatFactory> customDateFormats) {
    getConfiguration().setCustomDateFormats(customDateFormats);
    return this;
  }

  /**
   * Sets the arithmetic engine used to perform arithmetic operations.
   * The default is {@link ArithmeticEngine#BIGDECIMAL_ENGINE}.
   *
   * @param arithmeticEngine
   */
  public FreemarkerBuilder setArithmeticEngine(ArithmeticEngine arithmeticEngine) {
    getConfiguration().setArithmeticEngine(arithmeticEngine);
    return this;
  }

  /**
   * Informs FreeMarker about the charset used for the output. As FreeMarker outputs character stream (not
   * byte stream), it's not aware of the output charset unless the software that encloses it tells it
   * with this setting. Some templates may use FreeMarker features that require this information.
   * Setting this to {@code null} means that the output encoding is not known.
   *
   * <p>Defaults to {@code null} (unknown).
   *
   * @param outputEncoding
   */
  public FreemarkerBuilder setOutputEncoding(String outputEncoding) {
    getConfiguration().setOutputEncoding(outputEncoding);
    return this;
  }

  /**
   * Sets the URL escaping (URL encoding, percentage encoding) charset. If {@code null}, the output encoding
   * ({@link #setOutputEncoding(String)}) will be used for URL escaping.
   * <p>
   * Defaults to {@code null}.
   *
   * @param urlEscapingCharset
   */
  public FreemarkerBuilder setURLEscapingCharset(String urlEscapingCharset) {
    getConfiguration().setURLEscapingCharset(urlEscapingCharset);
    return this;
  }

  /**
   * Sets the {@link TemplateClassResolver} that is used when the
   * <code>new</code> built-in is called in a template. That is, when
   * a template contains the <code>"com.example.SomeClassName"?new</code>
   * expression, this object will be called to resolve the
   * <code>"com.example.SomeClassName"</code> string to a class. The default
   * value is {@link TemplateClassResolver#UNRESTRICTED_RESOLVER} in
   * FreeMarker 2.3.x, and {@link TemplateClassResolver#SAFER_RESOLVER}
   * starting from FreeMarker 2.4.0. If you allow users to upload templates,
   * it's important to use a custom restrictive {@link TemplateClassResolver}.
   *
   * <p>Note that the {@link MemberAccessPolicy} used by the {@link ObjectWrapper} also influences what constructors
   * are available. Allowing the resolution of the class here is not enough in itself, as the
   * {@link MemberAccessPolicy} has to allow exposing the particular constructor you try to call as well.
   *
   * @param newBuiltinClassResolver
   * @since 2.3.17
   */
  public FreemarkerBuilder setNewBuiltinClassResolver(TemplateClassResolver newBuiltinClassResolver) {
    getConfiguration().setNewBuiltinClassResolver(newBuiltinClassResolver);
    return this;
  }

  /**
   * Sets whether the output {@link Writer} is automatically flushed at
   * the end of {@link Template#process(Object, Writer)} (and its
   * overloads). The default is {@code true}.
   *
   * <p>Using {@code false} is needed for example when a Web page is composed
   * from several boxes (like portlets, GUI panels, etc.) that aren't inserted
   * with <tt>#include</tt> (or with similar directives) into a master
   * FreeMarker template, rather they are all processed with a separate
   * {@link Template#process(Object, Writer)} call. In a such scenario the
   * automatic flushes would commit the HTTP response after each box, hence
   * interfering with full-page buffering, and also possibly decreasing
   * performance with too frequent and too early response buffer flushes.
   *
   * @param autoFlush
   * @since 2.3.17
   */
  public FreemarkerBuilder setAutoFlush(boolean autoFlush) {
    getConfiguration().setAutoFlush(autoFlush);
    return this;
  }

  /**
   * Sets if tips should be shown in error messages of errors arising during template processing.
   * The default is {@code true}.
   *
   * @param showTips
   * @since 2.3.21
   */
  public FreemarkerBuilder setShowErrorTips(boolean showTips) {
    getConfiguration().setShowErrorTips(showTips);
    return this;
  }

  /**
   * Specifies if {@code ?api} can be used in templates. Defaults to {@code false} so that updating FreeMarker won't
   * decrease the security of existing applications.
   *
   * @param value
   * @since 2.3.22
   */
  public FreemarkerBuilder setAPIBuiltinEnabled(boolean value) {
    getConfiguration().setAPIBuiltinEnabled(value);
    return this;
  }

  /**
   * Specifies the algorithm used for {@code ?truncate}. Defaults to
   * {@link DefaultTruncateBuiltinAlgorithm#ASCII_INSTANCE}. Most customization needs can be addressed by
   * creating a new {@link DefaultTruncateBuiltinAlgorithm} with the proper constructor parameters. Otherwise users
   * my use their own {@link TruncateBuiltinAlgorithm} implementation.
   *
   * <p>In case you need to set this with {@link Properties}, or a similar configuration approach that doesn't let you
   * create the value in Java, see examples at {@link #setSetting(String, String)}.
   *
   * @param truncateBuiltinAlgorithm
   * @since 2.3.29
   */
  public FreemarkerBuilder setTruncateBuiltinAlgorithm(TruncateBuiltinAlgorithm truncateBuiltinAlgorithm) {
    getConfiguration().setTruncateBuiltinAlgorithm(truncateBuiltinAlgorithm);
    return this;
  }

  /**
   * Specifies if {@code <#import ...>} (and {@link Environment#importLib(String, String)}) should delay the loading
   * and processing of the imported templates until the content of the imported namespace is actually accessed. This
   * makes the overhead of <em>unused</em> imports negligible. Note that turning on lazy importing isn't entirely
   * transparent, as accessing global variables (usually created with {@code <#global ...=...>}) that should be
   * created by the imported template won't trigger the loading and processing of the lazily imported template
   * (because globals aren't accessed through the namespace variable), so the global variable will just be missing.
   * In general, you lose the strict control over when the namespace initializing code in the imported template will
   * be executed, though it shouldn't mater for most well designed imported templates.
   * Another drawback is that importing a missing or otherwise broken template will be successful, and the problem
   * will remain hidden until (and if) the namespace content is actually used. Note that the namespace initializing
   * code will run with the same {@linkplain Configurable#getLocale() locale} as it was at the point of the
   * {@code <#import ...>} call (other settings won't be handled specially like that).
   *
   * <p>
   * The default is {@code false} (and thus imports are eager) for backward compatibility, which can cause
   * perceivable overhead if you have many imports and only a few of them is actually used.
   *
   * <p>
   * This setting also affects {@linkplain #setAutoImports(Map) auto-imports}, unless you have set a non-{@code null}
   * value with {@link #setLazyAutoImports(Boolean)}.
   *
   * @param lazyImports
   * @see #setLazyAutoImports(Boolean)
   * @since 2.3.25
   */
  public FreemarkerBuilder setLazyImports(boolean lazyImports) {
    getConfiguration().setLazyImports(lazyImports);
    return this;
  }

  /**
   * Specifies if {@linkplain #setAutoImports(Map) auto-imports} will be
   * {@link #setLazyImports(boolean) lazy imports}. This is useful to make the overhead of <em>unused</em>
   * auto-imports negligible. If this is set to {@code null}, {@link Configuration#getLazyImports()} specifies the behavior of
   * auto-imports too. The default value is {@code null}.
   *
   * @param lazyAutoImports
   * @since 2.3.25
   */
  public FreemarkerBuilder setLazyAutoImports(Boolean lazyAutoImports) {
    getConfiguration().setLazyAutoImports(lazyAutoImports);
    return this;
  }

  /**
   * Removes all auto-imports, then calls {@link Configuration#addAutoImport(String, String)} for each {@link Map}-entry (the entry
   * key is the {@code namespaceVarName}). The order of the auto-imports will be the same as {@link Map#keySet()}
   * returns the keys (but the order of imports doesn't mater for properly designed libraries anyway).
   *
   * @param map Maps the namespace variable names to the template names; not {@code null}
   */
  public FreemarkerBuilder setAutoImports(Map map) {
    getConfiguration().setAutoImports(map);
    return this;
  }

  /**
   * Removes all auto-includes, then calls {@link Configuration#addAutoInclude(String)} for each {@link List} items.
   *
   * <p>Before {@linkplain Configuration#Configuration(Version) incompatible improvements} 2.3.25 it doesn't filter
   * out duplicates from the list if this method was called on a {@link Configuration} instance.
   *
   * @param templateNames
   */
  public FreemarkerBuilder setAutoIncludes(List templateNames) {
    getConfiguration().setAutoIncludes(templateNames);
    return this;
  }

//  /**
//   * @param strict
//   * @deprecated Set this on the {@link ObjectWrapper} itself.
//   */
//  public FreemarkerBuilder setStrictBeanModels(boolean strict) {
//    getConfiguration().setStrictBeanModels(strict);
//    return this;
//  }

  /**
   * Set the settings stored in a <code>Properties</code> object.
   *
   * @param props
   * @throws TemplateException if the <code>Properties</code> object contains
   *                           invalid keys, or invalid setting values, or any other error occurs
   *                           while changing the settings.
   */
  public FreemarkerBuilder setSettings(Properties props) {
    CatchUtils.tryThrow(() -> getConfiguration().setSettings(props));
    return this;
  }

  /**
   * Reads a setting list (key and element pairs) from the input stream.
   * The stream has to follow the usual <code>.properties</code> format.
   *
   * @param propsIn
   * @throws TemplateException if the stream contains
   *                           invalid keys, or invalid setting values, or any other error occurs
   *                           while changing the settings.
   * @throws IOException       if an error occurred when reading from the input stream.
   */
  public FreemarkerBuilder setSettings(InputStream propsIn) {
    CatchUtils.tryThrow(() -> getConfiguration().setSettings(propsIn));
    return this;
  }

  /**
   * Sets a named custom attribute for this configurable.
   *
   * @param name  the name of the custom attribute
   * @param value the value of the custom attribute. You can set the value to
   *              null, however note that there is a semantic difference between an
   *              attribute set to null and an attribute that is not present, see
   *              {@link Configuration#removeCustomAttribute(String)}.
   */
  public FreemarkerBuilder setCustomAttribute(String name, Object value) {
    getConfiguration().setCustomAttribute(name, value);
    return this;
  }

}
