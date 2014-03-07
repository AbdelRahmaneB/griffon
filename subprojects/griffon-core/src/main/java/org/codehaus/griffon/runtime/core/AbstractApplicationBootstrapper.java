/*
 * Copyright 2008-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.core;

import griffon.core.ApplicationBootstrapper;
import griffon.core.GriffonApplication;
import griffon.core.env.GriffonEnvironment;
import griffon.core.injection.Binding;
import griffon.core.injection.Injector;
import griffon.core.injection.InjectorFactory;
import griffon.core.injection.Module;
import griffon.util.GriffonClassUtils;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.AnnotationUtils.sortByDependencies;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractApplicationBootstrapper implements ApplicationBootstrapper {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultApplicationBootstrapper.class);
    private static final String INJECTOR = "injector";
    protected final GriffonApplication application;

    public AbstractApplicationBootstrapper(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
    }

    @Override
    public void bootstrap() throws Exception {
        // 1 initialize environment settings
        LOG.info("Griffon {}", GriffonEnvironment.getGriffonVersion());
        LOG.info("Build: {}", GriffonEnvironment.getBuildDateTime());
        LOG.info("JVM: {}", GriffonEnvironment.getJvmVersion());
        LOG.info("OS: {}", GriffonEnvironment.getOsVersion());

        // 2 create bindings
        LOG.debug("Creating module bindings");
        Iterable<Binding<?>> bindings = createBindings();

        if (LOG.isTraceEnabled()) {
            for (Binding<?> binding : bindings) {
                LOG.trace(binding.toString());
            }
        }

        // 3 create injector
        LOG.debug("Creating application injector");
        createInjector(bindings);
    }

    @Override
    public void run() {
        application.initialize();
        application.startup();
        application.ready();
    }

    @Nonnull
    protected Iterable<Binding<?>> createBindings() {
        Map<Key, Binding<?>> map = new LinkedHashMap<>();

        List<Module> modules = new ArrayList<>();
        createApplicationModule(modules);
        collectModuleBindings(modules);

        for (Module module : modules) {
            for (Binding<?> binding : module.getBindings()) {
                map.put(Key.of(binding), binding);
            }
        }

        return unmodifiableCollection(map.values());
    }

    protected void createApplicationModule(@Nonnull List<Module> modules) {
        modules.add(new AbstractModule() {
            @Override
            protected void doConfigure() {
                bind(GriffonApplication.class)
                    .toInstance(application);
            }
        });
    }

    protected void collectModuleBindings(@Nonnull Collection<Module> modules) {
        List<Module> moduleInstances = loadModules();
        moduleInstances.add(0, new DefaultApplicationModule());
        Map<String, Module> sortedModules = sortByDependencies(moduleInstances, "Module", "module");
        for (Map.Entry<String, Module> entry : sortedModules.entrySet()) {
            LOG.debug("Loading module bindings from {}:{}", entry.getKey(), entry.getValue());
            modules.add(entry.getValue());
        }
    }

    @Nonnull
    protected abstract List<Module> loadModules();

    private void createInjector(@Nonnull Iterable<Binding<?>> bindings) throws Exception {
        ServiceLoader<InjectorFactory> serviceLoader = ServiceLoader.load(InjectorFactory.class);
        try {
            Iterator<InjectorFactory> iterator = serviceLoader.iterator();
            InjectorFactory injectorFactory = iterator.next();
            LOG.debug("Injector will be created by {}", injectorFactory);
            Injector<?> injector = injectorFactory.createInjector(application, bindings);
            GriffonClassUtils.setProperty(application, INJECTOR, injector);
        } catch (Exception e) {
            LOG.error("An error occurred while initializing the injector", sanitize(e));
            throw e;
        }
    }
}
