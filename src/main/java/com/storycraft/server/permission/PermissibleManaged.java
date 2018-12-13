package com.storycraft.server.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.storycraft.StoryPlugin;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class PermissibleManaged extends PermissibleBase implements Permissible {

    private PermissibleBase parent;
    private PermissionManager permissionManager;

    private boolean loaded = false;

    private List<String> allowPermList;
    private List<String> blockPermList;

    private WrappedField<Map<String, PermissionAttachmentInfo>, PermissibleBase> permissionsField;

    private Map<String, PermissionAttachmentInfo> permissions;

    public PermissibleManaged(PermissionManager permissionManager, PermissibleBase parent) {
        super(null);

        this.parent = parent;
        this.permissionManager = permissionManager;

        this.allowPermList = new ArrayList<>();
        this.blockPermList = new ArrayList<>();

        this.permissionsField = Reflect.getField(PermissibleBase.class, "permissions");
        this.permissions = new HashMap<>();

        this.loaded = true;

        recalculatePermissions();
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

    public StoryPlugin getPlugin() {
        return permissionManager.getPlugin();
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
        return new HashSet<>(permissions.values());
    }

    @Override
    public boolean hasPermission(String arg0) {
        if (isAllowed(arg0))
            return true;

        if (isBlocked(arg0))
            return false;

        return parent.hasPermission(arg0);
    }

    public boolean isAllowed(String arg0) {
        return allowPermList.contains(arg0) || allowPermList.contains("*");
    }

    public boolean isAllowed(Permission arg0) {
        return isAllowed(arg0.getName());
    }

    public boolean isBlocked(String arg0) {
        return blockPermList.contains(arg0) || blockPermList.contains("*");
    }

    public boolean isBlocked(Permission arg0) {
        return isBlocked(arg0.getName());
    }

    @Override
    public boolean hasPermission(Permission arg0) {
        return hasPermission(arg0.getName());
    }

    @Override
    public boolean isPermissionSet(String arg0) {
        return isAllowed(arg0) || parent.isPermissionSet(arg0);
    }

    @Override
    public boolean isPermissionSet(Permission arg0) {
        return isAllowed(arg0) || parent.isPermissionSet(arg0);
    }

    @Override
    public void recalculatePermissions() {
        if (loaded) {
            parent.recalculatePermissions();
            updatePermissions();
        }
    }
    
    public void updatePermissions() {
        permissions.clear();
        Map<String, PermissionAttachmentInfo> map = permissionsField.get(parent);

        for (String permission : map.keySet()) {
            permissions.put(permission, new AttachmentInfoManaged(map.get(permission)));
        }
    }

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		parent.removeAttachment(arg0);
    }
    
    public class AttachmentInfoManaged extends PermissionAttachmentInfo {

        private PermissionAttachmentInfo parent;

        public AttachmentInfoManaged(PermissionAttachmentInfo parent) {
            super(PermissibleManaged.this, parent.getPermission(), parent.getAttachment(), parent.getValue());

            this.parent = parent;
        }

        @Override
        public PermissibleManaged getPermissible() {
            return (PermissibleManaged) super.getPermissible();
        }

        @Override
        public boolean getValue() {
            return getPermissible().hasPermission(getPermission()) || !getPermissible().isBlocked(getPermission()) && parent.getValue();
        }
    }

}