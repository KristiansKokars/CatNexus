package com.kristianskokars.catnexus.core.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.spec.DestinationSpec
import kotlinx.coroutines.launch

@SuppressLint("ComposableNaming")
@Composable
fun <T : DestinationSpec<*>> ResultRecipient<T, Int>.scrollToReturnedItemIndex(
    lazyGridState: LazyGridState,
    scrollOffset: Int = -240,
) {
    val scope = rememberCoroutineScope()

    onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> { /* Ignored */ }
            is NavResult.Value -> {
                // We put an offset to ensure the scrolled to item is not hidden behind the top bar
                // TODO: make it take the same size as an item actually is
                scope.launch {
                    if (isItemVisible(result.value, lazyGridState)) return@launch

                    lazyGridState.scrollToItem(result.value, scrollOffset = scrollOffset)
                }
            }
        }
    }
}

private fun isItemVisible(itemIndex: Int, lazyGridState: LazyGridState) =
    itemIndex > lazyGridState.firstVisibleItemIndex && itemIndex < lazyGridState.layoutInfo.visibleItemsInfo.last().index
