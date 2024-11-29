package cn.yurin.yutori

class ModuleReinstallException(
    module: String,
) : RuntimeException(module)

class ModuleAliasDuplicateException(
    alias: String,
) : RuntimeException(alias)