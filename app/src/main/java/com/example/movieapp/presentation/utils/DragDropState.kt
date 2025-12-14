package com.example.movieapp.presentation.utils

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DragDropState internal constructor(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onMove: suspend (Int, Int) -> Unit
) {
    var draggingItemKey by mutableStateOf<Any?>(null)
        private set

    var draggingItemOffset by mutableFloatStateOf(0f)
        private set

    val draggingItemIndex: Int?
        get() = draggingItemKey?.let { key ->
            state.layoutInfo.visibleItemsInfo.firstOrNull { it.key == key }?.index
        }

    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = draggingItemIndex?.let { index ->
            state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
        }

    fun onDragStart(offset: Offset) {
        state.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
            ?.also {
                draggingItemKey = it.key
            }
    }

    fun onDragInterrupted() {
        draggingItemKey = null
        draggingItemOffset = 0f
    }

    fun onDrag(offset: Offset) {
        draggingItemOffset += offset.y

        val myIndex = draggingItemIndex ?: return
        val currentDraggingItem = draggingItemLayoutInfo ?: return
        val startOffset = currentDraggingItem.offset + draggingItemOffset

        val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
            val center = startOffset + currentDraggingItem.size / 2
            item.index != myIndex && center in item.offset.toFloat()..(item.offset + item.size).toFloat()
        }

        if (targetItem != null) {
            val targetIndex = targetItem.index
            if (myIndex != targetIndex) {
                scope.launch {
                    onMove(myIndex, targetIndex)
                }
                draggingItemOffset = 0f
            }
        }
    }
}

@Composable
fun rememberDragDropState(
    lazyListState: LazyListState,
    onMove: suspend (Int, Int) -> Unit
): DragDropState {
    val scope = rememberCoroutineScope()
    return remember { DragDropState(lazyListState, scope, onMove) }
}