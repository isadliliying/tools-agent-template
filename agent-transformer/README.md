该模块是agnet的入口，主要负责插桩逻辑的处理

由于类加载的关系，fatjar情况下，该模块不能直接调用helper模块的类，但是在某些情况下可以反射调用：
当进入tranformer模块处理逻辑的classloader是 LaunchURLClassLoader 时，可以通过该loader来反射加载helper模块中的类