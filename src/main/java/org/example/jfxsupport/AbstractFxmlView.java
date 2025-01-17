package org.example.jfxsupport;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.util.ResourceBundle.getBundle;

/**
 * Base class for fxml-based view classes.
 * <p>
 * Supports annotation driven creation of FXML based view beans with {@link FXMLView}
 * </p>
 *
 *
 */
public abstract class AbstractFxmlView implements ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFxmlView.class);

	private final ObjectProperty<Object> presenterProperty;

	private final ResourceBundle bundle;

	private final URL resource;

	private final FXMLView annotation;

    /**
     * -- SETTER --
     *  Set the controller
     *
     * @param controller
     */
    @Setter
    private PrototypeController controller;

    /**
     * -- GETTER --
     *  Gets the fxml loader.
     *
     * @return the FXMLLoader
     */
    @Getter
    private FXMLLoader fxmlLoader;

	private ApplicationContext applicationContext;

	private String fxmlRoot;
	

	/**
	 * Instantiates a new abstract fxml view.
	 */
	public AbstractFxmlView() {
		LOGGER.debug("AbstractFxmlView construction");
		// Set the root path to package path
		final String filePathFromPackageName = PropertyReaderHelper.determineFilePathFromPackageName(getClass());
		setFxmlRootPath(filePathFromPackageName);
		annotation = getFXMLAnnotation();
		resource = getURLResource(annotation);
		presenterProperty = new SimpleObjectProperty<>();
		bundle = getResourceBundle(getBundleName());
	}

	/**
	 * Gets the URL resource. This will be derived from applied annotation value
	 * or from naming convention.
	 *
	 * @param annotation
	 *            the annotation as defined by inheriting class.
	 * @return the URL resource
	 */
	private URL getURLResource(final FXMLView annotation) {
		if (annotation != null && !annotation.value().equals("")) {
			return getClass().getResource(annotation.value());
		} else {
			return getClass().getResource(getFxmlPath());
		}
	}

	/**
	 * Gets the {@link FXMLView} annotation from inheriting class.
	 *
	 * @return the FXML annotation
	 */
	private FXMLView getFXMLAnnotation() {
		final Class<? extends AbstractFxmlView> theClass = this.getClass();
		final FXMLView annotation = theClass.getAnnotation(FXMLView.class);
		return annotation;
	}

	/**
	 * Creates the controller for type.
	 *
	 * @param type
	 *            the type
	 * @return the object
	 */
	private Object createControllerForType(final Class<?> type) {
		return applicationContext.getBean(type);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {

		if (this.applicationContext != null) {
			return;
		}

		this.applicationContext = applicationContext;
	}

	/**
	 * Sets the fxml root path.
	 *
	 * @param path
	 *            the new fxml root path
	 */
	private void setFxmlRootPath(final String path) {
		if (path.endsWith("/")) {
			fxmlRoot = path;
		} else {
			fxmlRoot = path + "/";
		}
	}

	/**
	 * Load synchronously.
	 *
	 * @param resource
	 *            the resource
	 * @param bundle
	 *            the bundle
	 * @return the FXML loader
	 * @throws IllegalStateException
	 *             the illegal state exception
	 */
	private FXMLLoader loadSynchronously(final URL resource, final ResourceBundle bundle, 
			final PrototypeController controller) throws IllegalStateException {

		final FXMLLoader loader = new FXMLLoader(resource, bundle);

		if (controller != null) {
			loader.setControllerFactory(new Callback<Class<?>, Object>() {
				@Override
				public Object call(Class<?> aClass) {
					return controller;
				}
			});
		} else {
			loader.setControllerFactory(this::createControllerForType);
		}

		try {
			loader.load();
		} catch (final IOException ex) {
			throw new IllegalStateException("Cannot load " + getConventionalName(), ex);
		}

		return loader;
	}

	/**
	 * Ensure fxml loader initialized.
	 */
	private void ensureFxmlLoaderInitialized() {

		if (fxmlLoader != null) {
			return;
		}

		fxmlLoader = loadSynchronously(resource, bundle, controller);
		presenterProperty.set(fxmlLoader.getController());
	}

	/**
	 * Initializes the view by loading the FXML (if not happened yet) and
	 * returns the top Node (parent) specified in the FXML file.
	 *
	 * @return the root view as determined from {@link FXMLLoader}.
	 */
	public Parent getView() {

		ensureFxmlLoaderInitialized();

		final Parent parent = fxmlLoader.getRoot();
		addCSSIfAvailable(parent);
		return parent;
	}

	/**
	 * Initializes the view synchronously and invokes the consumer with the
	 * created parent Node within the FX UI thread.
	 *
	 * @param consumer
	 *            - an object interested in received the {@link Parent} as
	 *            callback
	 */
	public void getView(final Consumer<Parent> consumer) {
		CompletableFuture.supplyAsync(this::getView, Platform::runLater).thenAccept(consumer);
	}

	/**
	 * Scene Builder creates for each FXML document a root container. This
	 * method omits the root container (e.g. {@link AnchorPane}) and gives you
	 * the access to its first child.
	 *
	 * @return the first child of the {@link AnchorPane} or null if there are no
	 *         children available from this view.
	 */
	public Node getViewWithoutRootContainer() {

		final ObservableList<Node> children = getView().getChildrenUnmodifiable();
		if (children.isEmpty()) {
			return null;
		}

		return children.listIterator().next();
	}

	/**
	 * Adds the CSS if available.
	 *
	 * @param parent
	 *            the parent
	 */
	void addCSSIfAvailable(final Parent parent) {

		// Read global css when available:
		final List<String> list = PropertyReaderHelper.get(applicationContext.getEnvironment(), "javafx.css");
		if (!list.isEmpty()) {
			list.forEach(css -> parent.getStylesheets().add(getClass().getResource(css).toExternalForm()));
		}

		addCSSFromAnnotation(parent, annotation);

		final URL uri = getClass().getResource(getStyleSheetName());
		if (uri == null) {
			return;
		}

		final String uriToCss = uri.toExternalForm();
		parent.getStylesheets().add(uriToCss);
	}

	/**
	 * Adds the CSS from annotation to parent.
	 *
	 * @param parent
	 *            the parent
	 * @param annotation
	 *            the annotation
	 */
	private void addCSSFromAnnotation(final Parent parent, final FXMLView annotation) {
		if (annotation != null && annotation.css().length > 0) {
			for (final String cssFile : annotation.css()) {
				final URL uri = getClass().getResource(cssFile);
				if (uri != null) {
					final String uriToCss = uri.toExternalForm();
					parent.getStylesheets().add(uriToCss);
					LOGGER.debug("css file added to parent: {}", cssFile);
				} else {
					LOGGER.warn("referenced {} css file could not be located", cssFile);
				}
			}
		}
	}

	/**
	 * Gets the style sheet name.
	 *
	 * @return the style sheet name
	 */
	private String getStyleSheetName() {
		return fxmlRoot + getConventionalName(".css");
	}

	/**
	 * In case the view was not initialized yet, the conventional fxml
	 * (airhacks.fxml for the AirhacksView and AirhacksPresenter) are loaded and
	 * the specified presenter / controller is going to be constructed and
	 * returned.
	 *
	 * @return the corresponding controller / presenter (usually for a
	 *         AirhacksView the AirhacksPresenter)
	 */
	public Object getPresenter() {

		ensureFxmlLoaderInitialized();

		return presenterProperty.get();
	}

	/**
	 * Does not initialize the view. Only registers the Consumer and waits until
	 * the the view is going to be created / the method FXMLView#getView or
	 * FXMLView#getViewAsync invoked.
	 *
	 * @param presenterConsumer
	 *            listener for the presenter construction
	 */
	public void getPresenter(final Consumer<Object> presenterConsumer) {

		presenterProperty.addListener(
				(final ObservableValue<? extends Object> o, final Object oldValue, final Object newValue) -> {
					presenterConsumer.accept(newValue);
				});
	}

	/**
	 * Gets the conventional name.
	 *
	 * @param ending
	 *            the suffix to append
	 * @return the conventional name with stripped ending
	 */
	private String getConventionalName(final String ending) {
		return getConventionalName() + ending;
	}

	/**
	 * Gets the conventional name.
	 *
	 * @return the name of the view without the "View" prefix in lowerCase. For
	 *         AirhacksView just airhacks is going to be returned.
	 */
	private String getConventionalName() {
		return stripEnding(getClass().getSimpleName().toLowerCase());
	}

	/**
	 * Gets the bundle name.
	 *
	 * @return the bundle name
	 */
	private String getBundleName() {
		if (!StringUtils.isEmpty(annotation)) {
			final String lbundle = annotation.bundle();
			LOGGER.debug("Annotated bundle: {}", lbundle);
			return lbundle;
		} else {
			final String lbundle = getClass().getPackage().getName() + "." + getConventionalName();
			LOGGER.debug("Bundle: {} based on conventional name.", lbundle);
			return lbundle;
		}
	}

	/**
	 * Strip ending.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the string
	 */
	private static String stripEnding(final String clazz) {

		if (!clazz.endsWith("view")) {
			return clazz;
		}

		return clazz.substring(0, clazz.lastIndexOf("view"));
	}

	/**
	 * Gets the fxml file path.
	 *
	 * @return the relative path to the fxml file derived from the FXML view.
	 *         e.g. The name for the AirhacksView is going to be
	 *         <PATH>/airhacks.fxml.
	 */

	final String getFxmlPath() {
		final String fxmlPath = fxmlRoot + getConventionalName(".fxml");
		LOGGER.debug("Determined fxmlPath: " + fxmlPath);
		return fxmlPath;
	}

	/**
	 * Gets the resource bundle or returns null.
	 *
	 * @param name
	 *            the name of the resource bundle.
	 * @return the resource bundle
	 */
	private ResourceBundle getResourceBundle(final String name) {
		try {
			LOGGER.debug("Resource bundle: " + name);
			return getBundle(name);
		} catch (final MissingResourceException ex) {
			LOGGER.debug("No resource bundle could be determined: " + ex.getMessage());
			return null;
		}
	}

	/**
	 * Gets the resource bundle.
	 *
	 * @return an existing resource bundle, or null
	 */
	public ResourceBundle getResourceBundle() {
		return bundle;
	}

    @Override
	public String toString() {
		return "AbstractFxmlView [presenterProperty=" + presenterProperty + ", bundle=" + bundle + ", resource="
				+ resource + ", fxmlRoot=" + fxmlRoot + "]";
	}

}
