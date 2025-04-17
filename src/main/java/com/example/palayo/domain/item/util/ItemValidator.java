package com.example.palayo.domain.item.util;

import com.example.palayo.common.exception.BaseException;
import com.example.palayo.common.exception.ErrorCode;
import com.example.palayo.domain.item.entity.Item;
import com.example.palayo.domain.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemValidator {

	private final ItemRepository itemRepository;

	// 삭제된 아이템을 제외하고 조회
	public Item getValidItem(Long itemId) {
		return itemRepository.findById(itemId)
			.filter(item -> item.getDeletedAt() == null)
			.orElseThrow(() -> new BaseException(ErrorCode.ITEM_NOT_FOUND, null));
	}

	// 아이템의 소유자와 현재 사용자(userId)가 일치하는지 검증
	public void validateOwnership(Item item, Long userId) {
		if (!item.getSeller().getId().equals(userId)) {
			throw new BaseException(ErrorCode.ITEM_EDIT_FORBIDDEN, null);
		}
	}
}