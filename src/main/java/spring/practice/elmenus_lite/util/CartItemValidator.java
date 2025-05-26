package spring.practice.elmenus_lite.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.practice.elmenus_lite.dto.CartItemDto;
import spring.practice.elmenus_lite.exception.BadRequestException;
import spring.practice.elmenus_lite.statusCode.ErrorMessage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartItemValidator {

    private final MenuItemRepository menuItemRepository;

    public List<MenuItemModel> validateMenuItems(List<CartItemDto> cartItemsDtos) {
        List<Integer> menuItemsIds = cartItemsDtos.stream().map(CartItemDto::getMenuItemId).toList();
        List<MenuItemModel> menuItemModels = menuItemRepository.findAllByIdIn(menuItemsIds);
        List<Integer> menusIdsNotExist = menuItemsIds.stream()
                .mapToInt(Integer::intValue)
                .filter(menuItemsId -> menuItemModels.stream()
                        .noneMatch(menuItemModel -> menuItemModel.getId().equals(menuItemsId)))
                .boxed().toList();
        if(!menusIdsNotExist.isEmpty()) {
            throw new BadRequestException(ErrorMessage.MENU_ITEM_IS_NOT_EXIST.getFinalMessage(menusIdsNotExist));
        }

        return menuItemModels;
    }
}
