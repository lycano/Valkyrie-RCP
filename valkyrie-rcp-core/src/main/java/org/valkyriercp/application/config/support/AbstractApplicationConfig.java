package org.valkyriercp.application.config.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.valkyriercp.application.*;
import org.valkyriercp.application.config.ApplicationConfig;
import org.valkyriercp.application.config.ApplicationLifecycleAdvisor;
import org.valkyriercp.application.config.ApplicationObjectConfigurer;
import org.valkyriercp.application.exceptionhandling.DelegatingExceptionHandler;
import org.valkyriercp.application.exceptionhandling.JXErrorDialogExceptionHandler;
import org.valkyriercp.application.exceptionhandling.RegisterableExceptionHandler;
import org.valkyriercp.application.exceptionhandling.SimpleExceptionHandlerDelegate;
import org.valkyriercp.application.session.ApplicationSession;
import org.valkyriercp.application.session.ApplicationSessionInitializer;
import org.valkyriercp.application.support.*;
import org.valkyriercp.binding.form.BindingErrorMessageProvider;
import org.valkyriercp.binding.form.FieldFaceSource;
import org.valkyriercp.binding.form.support.DefaultBindingErrorMessageProvider;
import org.valkyriercp.binding.form.support.MessageSourceFieldFaceSource;
import org.valkyriercp.binding.value.ValueChangeDetector;
import org.valkyriercp.binding.value.support.DefaultValueChangeDetector;
import org.valkyriercp.command.CommandConfigurer;
import org.valkyriercp.command.CommandManager;
import org.valkyriercp.command.CommandRegistry;
import org.valkyriercp.command.CommandServices;
import org.valkyriercp.command.config.DefaultCommandConfig;
import org.valkyriercp.command.config.DefaultCommandConfigurer;
import org.valkyriercp.command.support.DefaultCommandManager;
import org.valkyriercp.command.support.DefaultCommandRegistry;
import org.valkyriercp.command.support.DefaultCommandServices;
import org.valkyriercp.convert.support.CollectionToListModelConverter;
import org.valkyriercp.convert.support.ListToListModelConverter;
import org.valkyriercp.factory.*;
import org.valkyriercp.form.binding.BinderSelectionStrategy;
import org.valkyriercp.form.binding.BindingFactoryProvider;
import org.valkyriercp.form.binding.swing.SwingBinderSelectionStrategy;
import org.valkyriercp.form.binding.swing.SwingBindingFactoryProvider;
import org.valkyriercp.form.builder.ChainedInterceptorFactory;
import org.valkyriercp.form.builder.FormComponentInterceptorFactory;
import org.valkyriercp.image.DefaultIconSource;
import org.valkyriercp.image.DefaultImageSource;
import org.valkyriercp.image.IconSource;
import org.valkyriercp.image.ImageSource;
import org.valkyriercp.rules.RulesSource;
import org.valkyriercp.rules.reporting.DefaultMessageTranslatorFactory;
import org.valkyriercp.rules.reporting.MessageTranslatorFactory;
import org.valkyriercp.rules.support.DefaultRulesSource;
import org.valkyriercp.security.SecurityControllerManager;
import org.valkyriercp.security.support.DefaultSecurityControllerManager;
import org.valkyriercp.util.DialogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractApplicationConfig implements ApplicationConfig {
    @Autowired
    private ApplicationContext applicationContext;

    public ApplicationContext applicationContext() {
        return applicationContext;
    }

    @Bean
    public Application application() {
        return new DefaultApplication();
    }

    @Bean
    public ApplicationPageFactory applicationPageFactory() {
        return new DefaultApplicationPageFactory();
    }

    @Bean
    public ApplicationWindowFactory applicationWindowFactory() {
        return new DefaultApplicationWindowFactory();
    }

    @Bean
    public ApplicationLifecycleAdvisor applicationLifecycleAdvisor() {
        DefaultApplicationLifecycleAdvisor advisor = new DefaultApplicationLifecycleAdvisor();
        advisor.setCommandConfigClass(getCommandConfigClass());
        return advisor;
    }

    @Bean
    public ApplicationDescriptor applicationDescriptor() {
        DefaultApplicationDescriptor defaultApplicationDescriptor = new DefaultApplicationDescriptor();
        applicationObjectConfigurer().configure(defaultApplicationDescriptor, "applicationDescriptor");
        return defaultApplicationDescriptor;
    }

    @Bean
    public ImageSource imageSource() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocations(getImageSourceResources().values().toArray(new Resource[getImageSourceResources().size()]));
        DefaultImageSource imageSource = null;
        try {
            propertiesFactoryBean.afterPropertiesSet();
            imageSource = new DefaultImageSource(propertiesFactoryBean.getObject());
        } catch (IOException e) {
            throw new IllegalArgumentException("Error getting imagesource property file", e);
        }
        imageSource.setBrokenImageIndicator(applicationContext().getResource("classpath:/org/valkyriercp/images/alert/error_obj.gif"));
        return imageSource;
    }

    public Map<String, Resource> getImageSourceResources() {

        Map<String, Resource> resources = new HashMap<String, Resource>();
        resources.put("default", applicationContext().getResource("classpath:/org/valkyriercp/images/images.properties"));
        return resources;
    }

    @Bean
    public WindowManager windowManager() {
        return new WindowManager();
    }

    @Bean
    public CommandServices commandServices() {
        return new DefaultCommandServices();
    }

    @Bean
    public CommandConfigurer commandConfigurer() {
        return new DefaultCommandConfigurer();
    }

    @Bean
    public CommandRegistry commandRegistry() {
        return new DefaultCommandRegistry();
    }

    @Bean
    public PageComponentPaneFactory pageComponentPaneFactory() {
        return new DefaultPageComponentPaneFactory();
    }

    @Bean
    public ViewDescriptorRegistry viewDescriptorRegistry() {
        return new BeanFactoryViewDescriptorRegistry();
    }

    @Bean
    public IconSource iconSource() {
        return new DefaultIconSource();
    }

    @Bean
    public ComponentFactory componentFactory() {
        return new DefaultComponentFactory();
    }

    @Bean
    public ButtonFactory buttonFactory() {
        return new DefaultButtonFactory();
    }

    @Bean
    public MenuFactory menuFactory() {
        return new DefaultMenuFactory();
    }

    @Bean
    public ButtonFactory toolbarButtonFactory() {
        return new DefaultButtonFactory();
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor() {
        return new MessageSourceAccessor(messageSource());
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(getResourceBundleLocations().toArray(new String[getResourceBundleLocations().size()]));
        return messageSource;
    }

    public List<String> getResourceBundleLocations() {
        ArrayList<String> list =  new ArrayList<String>();
        list.add("org.valkyriercp.messages.default");
        return list;
    }

    @Bean
    public ApplicationObjectConfigurer applicationObjectConfigurer() {
        return new DefaultApplicationObjectConfigurer();
    }

    @Bean
    public SecurityControllerManager securityControllerManager() {
        return new DefaultSecurityControllerManager();
    }

    public Class<?> getCommandConfigClass() {
        return DefaultCommandConfig.class;
    }

    @Bean
    public RegisterableExceptionHandler registerableExceptionHandler() {
        JXErrorDialogExceptionHandler errorDialogExceptionHandler = new JXErrorDialogExceptionHandler();
        DelegatingExceptionHandler handler = new DelegatingExceptionHandler();
        handler.getDelegateList().add(new SimpleExceptionHandlerDelegate(Throwable.class, errorDialogExceptionHandler));
        return handler;
    }

    @Bean
    public ApplicationSession applicationSession() {
        return new ApplicationSession();
    }

    @Bean
    public ApplicationSessionInitializer applicationSessionInitializer() {
        return new ApplicationSessionInitializer();
    }

    @Bean
    public MessageResolver messageResolver() {
        return new MessageResolver();
    }

    @Bean
    public CommandManager commandManager() {
        return new DefaultCommandManager();
    }

    @Bean
    public ValueChangeDetector valueChangeDetector() {
        return new DefaultValueChangeDetector();
    }

    @Bean
    public MessageTranslatorFactory messageTranslatorFactory() {
        return new DefaultMessageTranslatorFactory();
    }

    @Bean
    public RulesSource rulesSource() {
        return new DefaultRulesSource();
    }

    @Bean
    public FieldFaceSource fieldFaceSource() {
        return new MessageSourceFieldFaceSource();
    }

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService conversionService =  new DefaultConversionService();
        conversionService.addConverter(new ListToListModelConverter());
        conversionService.addConverter(new CollectionToListModelConverter());
        return conversionService;
    }

    @Bean
    public FormComponentInterceptorFactory formComponentInterceptorFactory() {
        return new ChainedInterceptorFactory();
    }

    @Bean
    public BinderSelectionStrategy binderSelectionStrategy() {
        return new SwingBinderSelectionStrategy();
    }

    @Bean
    public BindingFactoryProvider bindingFactoryProvider() {
        return new SwingBindingFactoryProvider();
    }

    @Bean
    public BindingErrorMessageProvider bindingErrorMessageProvider() {
        return new DefaultBindingErrorMessageProvider();
    }

    @Bean
    public DialogFactory dialogFactory() {
        return new DialogFactory();
    }
}