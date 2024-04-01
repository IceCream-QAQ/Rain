package rain.api.permission

interface IUser {

    /*** 检查当前用户是否具有权限
     * @param permission 权限节点
     */
    fun checkPermission(permission: String): Boolean

}