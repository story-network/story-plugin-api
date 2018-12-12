package com.storycraft.server.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class PermissibleManaged extends PermissibleBase implements Permissible {

    private PermissibleBase parent;
    private PermissionManager permissionManager;

    private List<String> allowPermList;
    private List<String> blockPermList;

    public PermissibleManaged(PermissionManager permissionManager, PermissibleBase parent) {
        super(null);

        this.parent = parent;
        this.permissionManager = permissionManager;

        this.allowPermList = new ArrayList<>();
        this.blockPermList = new ArrayList<>();
    }

    @Override
    public boolean isOp() {
        return parent.isOp();
    }

    @Override
    public void setOp(boolean arg0) {
        parent.setOp(arg0);
    }

    public PermissibleBase getParent() {
        return parent;
    }

    protected void setAllowPermList(List<String> allowPermList) {
        this.allowPermList = allowPermList;
    }

    protected void setBlockPermList(List<String> blockPermList) {
        this.blockPermList = blockPermList;
    }

    protected List<String> getAllowPermList() {
        return this.allowPermList;
    }

    protected List<String> setBlockPermList() {
        return this.blockPermList;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0) {
        return parent.addAttachment(arg0);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
        return parent.addAttachment(arg0, arg1);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
        return parent.addAttachment(arg0, arg1, arg2);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
        return parent.addAttachment(arg0, arg1, arg2, arg3);
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return parent.getEffectivePermissions();
    }

    @Override
    public boolean hasPermission(String arg0) {
        if (allowPermList.contains("*") || allowPermList.contains(arg0))
            return true;

        if (blockPermList.contains(arg0) || blockPermList.contains("*"))
            return false;

        return parent.hasPermission(arg0);
    }

    @Override
    public boolean hasPermission(Permission arg0) {
        return hasPermission(arg0.getName());
    }

    @Override
    public boolean isPermissionSet(String arg0) {
        return parent.isPermissionSet(arg0);
    }

    @Override
    public boolean isPermissionSet(Permission arg0) {
        return parent.isPermissionSet(arg0);
    }

    @Override
    public void recalculatePermissions() {

	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		parent.removeAttachment(arg0);
	}

}