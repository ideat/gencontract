package mindware.com.model;

public class MenuOption {
    private Integer menuOptionId;
    private Integer optionId;
    private Integer rolId;
    private Option option;

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public Integer getMenuOptionId() {
        return menuOptionId;
    }

    public void setMenuOptionId(Integer menuOptionId) {
        this.menuOptionId = menuOptionId;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }
}
