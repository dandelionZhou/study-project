#### AnnotationConfigApplicationContext
##### 1.加载指定配置文件new AnnotationConfigApplicationContext(MyConfig.class);
```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
	this();
	register(componentClasses);
    //刷新容器
	refresh();
} 
```java
@Override
public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
		// Prepare this context for refreshing.
		prepareRefresh();

		// Tell the subclass to refresh the internal bean factory.
		ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

		// Prepare the bean factory for use in this context.
		prepareBeanFactory(beanFactory);

		try {
			// Allows post-processing of the bean factory in context subclasses.
			postProcessBeanFactory(beanFactory);

			// Invoke factory processors registered as beans in the context.
			invokeBeanFactoryPostProcessors(beanFactory);

			// Register bean processors that intercept bean creation.
			registerBeanPostProcessors(beanFactory);

			// Initialize message source for this context.
			initMessageSource();

			// Initialize event multicaster for this context.
			initApplicationEventMulticaster();

			// Initialize other special beans in specific context subclasses.
			onRefresh();

			// Check for listener beans and register them.
			registerListeners();

			// Instantiate all remaining (non-lazy-init) singletons.
            //实例化所有的单实例bean
			finishBeanFactoryInitialization(beanFactory);

			// Last step: publish corresponding event.
			finishRefresh();
		}

		catch (BeansException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception encountered during context initialization - " +
				"cancelling refresh attempt: " + ex);
			}

			// Destroy already created singletons to avoid dangling resources.
			destroyBeans();

			// Reset 'active' flag.
			cancelRefresh(ex);

			// Propagate exception to caller.
			throw ex;
		}

		finally {
			// Reset common introspection caches in Spring's core, since we
			// might not ever need metadata for singleton beans anymore...
			resetCommonCaches();
		}
	}
}
```
##### 3.AnnotationConfigApplicationContext.finishBeanFactoryInitialization() 实例化所有单实例Bean
```java
/**
 * Finish the initialization of this context's bean factory,
 * initializing all remaining singleton beans.
 */
	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
	// Initialize conversion service for this context.
	if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
	beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
		beanFactory.setConversionService(
		beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
	}

	// Register a default embedded value resolver if no bean post-processor
	// (such as a PropertyPlaceholderConfigurer bean) registered any before:
	// at this point, primarily for resolution in annotation attribute values.
	if (!beanFactory.hasEmbeddedValueResolver()) {
		beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
	}

	// Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
	String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
	for (String weaverAwareName : weaverAwareNames) {
		getBean(weaverAwareName);
	}

	// Stop using the temporary ClassLoader for type matching.
	beanFactory.setTempClassLoader(null);

	// Allow for caching all bean definition metadata, not expecting further changes.
	beanFactory.freezeConfiguration();

	// Instantiate all remaining (non-lazy-init) singletons.
    //默认工厂容器DefaultListableBeanFactory实例化单实例Bean
	beanFactory.preInstantiateSingletons();
}
```
##### 4.DefaultListableBeanFactory.preInstantiateSingletons()实例化Bean
```java
	@Override
	public void preInstantiateSingletons() throws BeansException {
		if (logger.isTraceEnabled()) {
			logger.trace("Pre-instantiating singletons in " + this);
		}

		// Iterate over a copy to allow for init methods which in turn register new bean definitions.
		// While this may not be part of the regular factory bootstrap, it does otherwise work fine.获取所有的BeanDefinition
		List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

		// 循环通过getBean创建Bean
		for (String beanName : beanNames) {
			RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
			if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
				if (isFactoryBean(beanName)) {
					Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
					if (bean instanceof FactoryBean) {
						FactoryBean<?> factory = (FactoryBean<?>) bean;
						boolean isEagerInit;
						if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
							isEagerInit = AccessController.doPrivileged(
									(PrivilegedAction<Boolean>) ((SmartFactoryBean<?>) factory)::isEagerInit,
									getAccessControlContext());
						}
						else {
							isEagerInit = (factory instanceof SmartFactoryBean &&
									((SmartFactoryBean<?>) factory).isEagerInit());
						}
						if (isEagerInit) {
							getBean(beanName);
						}
					}
				}
				else {
					getBean(beanName);
				}
			}
		}

		// Trigger post-initialization callback for all applicable beans...
		for (String beanName : beanNames) {
			Object singletonInstance = getSingleton(beanName);
			if (singletonInstance instanceof SmartInitializingSingleton) {
				StartupStep smartInitialize = this.getApplicationStartup().start("spring.beans.smart-initialize")
						.tag("beanName", beanName);
				SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
				if (System.getSecurityManager() != null) {
					AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
						smartSingleton.afterSingletonsInstantiated();
						return null;
					}, getAccessControlContext());
				}
				else {
					smartSingleton.afterSingletonsInstantiated();
				}
				smartInitialize.end();
			}
		}
	}
```
##### 5.AbstractBeanFactory.getBean()调用doGetBean获取Bean实例
```java
@Override
public Object getBean(String name) throws BeansException {
	return doGetBean(name, null, null, false);
}
```
##### 6.AbstractBeanFactory.doGetBean
```java
protected <T> T doGetBean(
	String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
		throws BeansException {

	String beanName = transformedBeanName(name);
	Object bean;
	
	// Eagerly check singleton cache for manually registered singletons.
	//解决循环依赖的重要方法，从提前暴露的缓存中查询是否存在该bean的实例
	Object sharedInstance = getSingleton(beanName);
	if (sharedInstance != null && args == null) {
		if (logger.isTraceEnabled()) {
			if (isSingletonCurrentlyInCreation(beanName)) {
				logger.trace("Returning eagerly cached instance of singleton bean '" + beanName +
						"' that is not fully initialized yet - a consequence of a circular reference");
				}
			else {
					logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
				}
		}
		bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
	}
	else {
	...省略若干代码
	// Create bean instance.
	if (mbd.isSingleton()) {
                //createBean创建实例
		sharedInstance = getSingleton(beanName, () -> {
			try {
				return createBean(beanName, mbd, args);
			}
			catch (BeansException ex) {
				// Explicitly remove instance from singleton cache: It might have been put there
				// eagerly by the creation process, to allow for circular reference resolution.
				// Also remove any beans that received a temporary reference to the bean.
				destroySingleton(beanName);
				throw ex;
			}
		});
		bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
	}
	...省略若干代码
	return (T) bean;
}
```

##### 7.DefaultSingletonBeanRegistry.getSingleton从缓存中获取bean
```java
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
	// Quick check for existing instance without full singleton lock
	//查看已完成初始化的实例缓存中是否存在该实例
	Object singletonObject = this.singletonObjects.get(beanName);
	if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
		//查看已完成实例化但未完成初始化的实例缓存中是否存在该实例
		singletonObject = this.earlySingletonObjects.get(beanName);		
		if (singletonObject == null && allowEarlyReference) {
			synchronized (this.singletonObjects) {
				// Consistent creation of early reference within full singleton lock
				singletonObject = this.singletonObjects.get(beanName);
				if (singletonObject == null) {
					singletonObject = this.earlySingletonObjects.get(beanName);
					if (singletonObject == null) {
						//查看实例化工厂中是否存在该实例
						ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
						if (singletonFactory != null) {
							singletonObject = singletonFactory.getObject();
							this.earlySingletonObjects.put(beanName, singletonObject);
							this.singletonFactories.remove(beanName);
						}
					}
				}
			}
		}
	}
	return singletonObject;
}
```
##### 8.AbstractAutowireCapableBeanFactory.createBean
```java 
/**
 * Central method of this class: creates a bean instance,
 * populates the bean instance, applies post-processors, etc.
 * @see #doCreateBean
 */
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
throws BeanCreationException {

	if (logger.isTraceEnabled()) {
		logger.trace("Creating instance of bean '" + beanName + "'");
	}
	RootBeanDefinition mbdToUse = mbd;

	// Make sure bean class is actually resolved at this point, and
	// clone the bean definition in case of a dynamically resolved Class
	// which cannot be stored in the shared merged bean definition.
	Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
	if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
		mbdToUse = new RootBeanDefinition(mbd);
		mbdToUse.setBeanClass(resolvedClass);
	}

	// Prepare method overrides.
	try {
		mbdToUse.prepareMethodOverrides();
	}
	catch (BeanDefinitionValidationException ex) {
		throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),
		beanName, "Validation of method overrides failed", ex);
	}

	try {
		// 给BeanPostProcessors一个返回代理对象的机会(AOP?)
		Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
		if (bean != null) {
			return bean;
		}
	}
	catch (Throwable ex) {
		throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
		"BeanPostProcessor before instantiation of bean failed", ex);
	}

	try {
        //否则调用doCreateBean创建实例
		Object beanInstance = doCreateBean(beanName, mbdToUse, args);
		if (logger.isTraceEnabled()) {
			logger.trace("Finished creating instance of bean '" + beanName + "'");
		}
		return beanInstance;
	}
	catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
		// A previously detected exception with proper bean creation context already,
		// or illegal singleton state to be communicated up to DefaultSingletonBeanRegistry.
		throw ex;
	}
	catch (Throwable ex) {
		throw new BeanCreationException(
		mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
	}
}
```
##### 9.AbstractAutowireCapableBeanFactory.doCreateBean
```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
throws BeanCreationException {

	// Instantiate the bean.
	BeanWrapper instanceWrapper = null;
	if (mbd.isSingleton()) {
		instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
	}
	if (instanceWrapper == null) {
		instanceWrapper = createBeanInstance(beanName, mbd, args);
	}
	Object bean = instanceWrapper.getWrappedInstance();
	Class<?> beanType = instanceWrapper.getWrappedClass();
	if (beanType != NullBean.class) {
		mbd.resolvedTargetType = beanType;
	}

	// Allow post-processors to modify the merged bean definition.
	synchronized (mbd.postProcessingLock) {
		if (!mbd.postProcessed) {
			try {
				applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
			}
			catch (Throwable ex) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
				"Post-processing of merged bean definition failed", ex);
			}
			mbd.postProcessed = true;
		}
	}

	// Eagerly cache singletons to be able to resolve circular references
	// even when triggered by lifecycle interfaces like BeanFactoryAware.
	// 查看earlySingletonObjects实例缓存中是否存在该实例
	boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
	isSingletonCurrentlyInCreation(beanName));
	// 如果earlySingletonObjects缓存中存在
	if (earlySingletonExposure) {
		if (logger.isTraceEnabled()) {
			logger.trace("Eagerly caching bean '" + beanName +
			"' to allow for resolving potential circular references");
		}
		
		//this.singletonFactories.put(beanName, singletonFactory);
		//this.earlySingletonObjects.remove(beanName);
		//this.registeredSingletons.add(beanName);
		addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
	}

	// Initialize the bean instance.
	Object exposedObject = bean;
	try {
		//解决循环依赖
		//初始化Bean的属性,这时如果存在A依赖B且B实例还未创建，则会回到第5步创建B实例
        //此时A实例在singletonFactories，registeredSingletons都是存在的
		populateBean(beanName, mbd, instanceWrapper);
		//调用自定义的BeanPostProcessor,InitializingBean，@PostContruct，@Bean(init-method)的初始化方法
		exposedObject = initializeBean(beanName, exposedObject, mbd);
	}
	catch (Throwable ex) {
		if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
			throw (BeanCreationException) ex;
		}
		else {
			throw new BeanCreationException(
			mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
		}
	}

	if (earlySingletonExposure) {
		Object earlySingletonReference = getSingleton(beanName, false);
		if (earlySingletonReference != null) {
			if (exposedObject == bean) {
				exposedObject = earlySingletonReference;
			}
			else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
				String[] dependentBeans = getDependentBeans(beanName);
				Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
				for (String dependentBean : dependentBeans) {
					if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
						actualDependentBeans.add(dependentBean);
					}
				}
				if (!actualDependentBeans.isEmpty()) {
				throw new BeanCurrentlyInCreationException(beanName,
				"Bean with name '" + beanName + "' has been injected into other beans [" +
				StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
				"] in its raw version as part of a circular reference, but has eventually been " +
				"wrapped. This means that said other beans do not use the final version of the " +
				"bean. This is often the result of over-eager type matching - consider using " +
				"'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
				}
			}
		}
	}

	// Register bean as disposable.
	try {
		registerDisposableBeanIfNecessary(beanName, bean, mbd);
	}
	catch (BeanDefinitionValidationException ex) {
		throw new BeanCreationException(
			mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
	}

	return exposedObject;
}
```

##### 10.AbstractAutowireCapableBeanFactory.populateBean 初始化bean的属性
```java 
/**
 * Populate the bean instance in the given BeanWrapper with the property values
 * from the bean definition.
 * @param beanName the name of the bean
 * @param mbd the bean definition for the bean
 * @param bw the BeanWrapper with bean instance
 */
@SuppressWarnings("deprecation")  // for postProcessPropertyValues
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
	if (bw == null) {
		if (mbd.hasPropertyValues()) {
			throw new BeanCreationException(
				mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
		}
		else {
			// Skip property population phase for null instance.
			return;
		}
	}

	// Give any InstantiationAwareBeanPostProcessors the opportunity to modify the
	// state of the bean before properties are set. This can be used, for example,
	// to support styles of field injection.
	//调用自定义的InstantiationAwareBeanPostProcessor实例化后处理器方法
	if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
		for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
			if (!bp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
				return;
			}
		}
	}

	PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);

	int resolvedAutowireMode = mbd.getResolvedAutowireMode();
	if (resolvedAutowireMode == AUTOWIRE_BY_NAME || resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
		MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
		// Add property values based on autowire by name if applicable.
		if (resolvedAutowireMode == AUTOWIRE_BY_NAME) {
			autowireByName(beanName, mbd, bw, newPvs);
		}
		// Add property values based on autowire by type if applicable.
		if (resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
			autowireByType(beanName, mbd, bw, newPvs);
		}
		pvs = newPvs;
	}

	boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
	boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);

	PropertyDescriptor[] filteredPds = null;
	if (hasInstAwareBpps) {
		if (pvs == null) {
			pvs = mbd.getPropertyValues();
		}
		for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
			//这时检测到A有个B的属性尚未创建便会调用第5步getBean(B)
			//创建B到了这步时，发现B中有个属性A需要注入，然后又调用getBean(A)获取A的实例
			//然后在getSinglon()时从singletonFactories缓存中发现存在该实例，因此直接返回A实例
			//B中的A属性已初始化完成，B创建完成后，返回到A的属性初始化那里，这时已经有了B实例
			//循环依赖已解决
			PropertyValues pvsToUse = bp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
			if (pvsToUse == null) {
				if (filteredPds == null) {
					filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
				}
				pvsToUse = bp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
				if (pvsToUse == null) {
					return;
				}
			}
			pvs = pvsToUse;
		}
	}
	if (needsDepCheck) {
		if (filteredPds == null) {
			filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
		}
		checkDependencies(beanName, mbd, filteredPds, pvs);
	}

	if (pvs != null) {
		applyPropertyValues(beanName, mbd, bw, pvs);
	}
}

//循环依赖用到的三级缓存
class DefaultSingletonBeanRegistry {
	/** Cache of singleton objects: bean name to bean instance. */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

	/** Cache of singleton factories: bean name to ObjectFactory. */
	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

	/** Cache of early singleton objects: bean name to bean instance. */
	private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
}
```

##### 11.AbstractAutowireCapableBeanFactory.initializeBean
```java
/**
 * Initialize the given bean instance, applying factory callbacks
 * as well as init methods and bean post processors.
 * <p>Called from {@link #createBean} for traditionally defined beans,
 * and from {@link #initializeBean} for existing bean instances.
 * @param beanName the bean name in the factory (for debugging purposes)
 * @param bean the new bean instance we may need to initialize
 * @param mbd the bean definition that the bean was created with
 * (can also be {@code null}, if given an existing bean instance)
 * @return the initialized bean instance (potentially wrapped)
 * @see BeanNameAware
 * @see BeanClassLoaderAware
 * @see BeanFactoryAware
 * @see #applyBeanPostProcessorsBeforeInitialization
 * @see #invokeInitMethods
 * @see #applyBeanPostProcessorsAfterInitialization
 */
	protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
    //如果bean实现了BeanNameAware，BeanClassLoaderAware，BeanFactoryAware
    //则通过反射调用setBeanName,setBeanClassLoader,setBeanFactory
    //将beanName,beanClassLoader,beanFactory注入bean实例
	if (System.getSecurityManager() != null) {
		AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
			invokeAwareMethods(beanName, bean);
			return null;
		}, getAccessControlContext());
	}
	else {
		invokeAwareMethods(beanName, bean);
	}

	Object wrappedBean = bean;
	if (mbd == null || !mbd.isSynthetic()) {
        //循环遍历实现了BeanPostProcessor的实例，执行其postProcessBeforeInitialization方法
		wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
	}

	try {
		invokeInitMethods(beanName, wrappedBean, mbd);
	}
	catch (Throwable ex) {
		throw new BeanCreationException(
			(mbd != null ? mbd.getResourceDescription() : null),
			beanName, "Invocation of init method failed", ex);
	}
	
	if (mbd == null || !mbd.isSynthetic()) {
		wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
	}

	return wrappedBean;
}
```
##### 12.AbstractAutowireCapableBeanFactory.invokeAwareMethods
```java
private void invokeAwareMethods(String beanName, Object bean) {
	if (bean instanceof Aware) {
		if (bean instanceof BeanNameAware) {
			((BeanNameAware) bean).setBeanName(beanName);
		}
		if (bean instanceof BeanClassLoaderAware) {
				ClassLoader bcl = getBeanClassLoader();
			if (bcl != null) {
				((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
			}
		}
		if (bean instanceof BeanFactoryAware) {
			((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
		}
	}
}
```
##### 13.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization
在Bean初始化前，执行所有实现BeanPostProcessor接口的后处理器的postProcessBeforeInitialization方法
```java
@Override
public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
throws BeansException {
	Object result = existingBean;
	//循环遍历所有的BeanPostProcessor实例
	for (BeanPostProcessor processor : getBeanPostProcessors()) {
		//执行初始化前方法后，返回当前bean
		Object current = processor.postProcessBeforeInitialization(result, beanName);
		if (current == null) {
			return result;
		}
		result = current;
	}
	return result;
}
```
##### 14.AbstractAutowireCapableBeanFactory.invokeInitMethods
执行bean自定义的初始化方法
+ 实现了InitializingBean
+ 在@Bean时指定的initMethod方法
+ @PostConstruct注解标志的方法
```java
/**
 * Give a bean a chance to react now all its properties are set,
 * and a chance to know about its owning bean factory (this object).
 * This means checking whether the bean implements InitializingBean or defines
 * a custom init method, and invoking the necessary callback(s) if it does.
 * @param beanName the bean name in the factory (for debugging purposes)
 * @param bean the new bean instance we may need to initialize
 * @param mbd the merged bean definition that the bean was created with
 * (can also be {@code null}, if given an existing bean instance)
 * @throws Throwable if thrown by init methods or by the invocation process
 * @see #invokeCustomInitMethod
 */
protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd)
	throws Throwable {

	//判断是否实现InitializingBean
	boolean isInitializingBean = (bean instanceof InitializingBean);
	//如果实现了InitializingBean，则调用bean定义的afterPropertiesSet
	if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
		if (logger.isTraceEnabled()) {
			logger.trace("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
		}
		if (System.getSecurityManager() != null) {
			try {				
				AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
					((InitializingBean) bean).afterPropertiesSet();
						return null;
				}, getAccessControlContext());
			}
			catch (PrivilegedActionException pae) {
				throw pae.getException();
			}
		}
		else {
		((InitializingBean) bean).afterPropertiesSet();
		}
	}
	
	if (mbd != null && bean.getClass() != NullBean.class) {
		//获取是否bean指定了InitMethod
		String initMethodName = mbd.getInitMethodName();
		//如果指定了InitMethod，则调用initMethod指定的初始化方法
		if (StringUtils.hasLength(initMethodName) &&
		!(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) &&
		!mbd.isExternallyManagedInitMethod(initMethodName)) {
			invokeCustomInitMethod(beanName, bean, mbd);
		}
	}
}
```
##### 15.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsAfterInitialization
bean初始化完成后，调用所有实现了BeanPostProcessor的后处理器的afterInitialization方法
```java
@Override
public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
		throws BeansException {

	Object result = existingBean;
	for (BeanPostProcessor processor : getBeanPostProcessors()) {
		Object current = processor.postProcessAfterInitialization(result, beanName);
		if (current == null) {
			return result;
		}
		result = current;
	}
	return result;
}
```