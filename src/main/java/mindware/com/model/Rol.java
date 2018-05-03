package mindware.com.model;

import java.util.List;

public class Rol {
    private Integer rolId;
    private String rolName;
    private String description;
    private List<MenuOption> menuOption;

    public List<MenuOption> getMenuOption() {
        return menuOption;
    }

    public void setMenuOption(List<MenuOption> menuOption) {
        this.menuOption = menuOption;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public String getRolName() {
        return rolName;
    }

    public void setRolName(String rolName) {
        this.rolName = rolName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
