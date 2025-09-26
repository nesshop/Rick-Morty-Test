package com.ernesto.rickandmortycompose.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ernesto.rickandmortycompose.core.navigation.Route.*
import com.ernesto.rickandmortycompose.feature.characters.ui.characterlist.CharactersListScreen
import com.ernesto.rickandmortycompose.feature.characters.ui.detail.DetailScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = CharacterList) {
        composable<CharacterList> {
            CharactersListScreen(navigateToDetail = { character ->
                navController.navigate(CharacterDetail(character.id))
            })
        }
        composable<CharacterDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<CharacterDetail>()
            DetailScreen(characterId = args.id, onBackPressed = { navController.popBackStack()  })
        }
    }
}