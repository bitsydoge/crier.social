package com.cold0.crier.social.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cold0.crier.social.theme.CrierSocialTheme
import com.cold0.crier.social.ui.ScreenNavitationsItemsBottomBar

@Composable
fun BottomBar(navController: NavController) {
	Column {
		Divider()
		BottomNavigation(
			elevation = 0.dp,
			backgroundColor = MaterialTheme.colors.surface,
			contentColor = MaterialTheme.colors.onSurface,
		) {
			val navBackStackEntry = navController.currentBackStackEntry
			val currentRoute = navBackStackEntry?.destination?.route
			ScreenNavitationsItemsBottomBar.forEach { item ->
				BottomNavigationItem(
					icon = { Icon(item.icon, contentDescription = item.title) },
					selected = currentRoute == item.route,
					onClick = {
						navController.navigate(item.route) {
							navController.graph.startDestinationRoute?.let { route ->
								popUpTo(route) {
									saveState = true
								}
							}
							launchSingleTop = true
							restoreState = true
						}
					}
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
	CrierSocialTheme(darkTheme = false) {
		BottomBar(rememberNavController())
	}
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreviewDark() {
	CrierSocialTheme(darkTheme = true) {
		BottomBar(rememberNavController())
	}
}