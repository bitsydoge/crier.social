package com.cold0.crier.social.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.cold0.crier.social.NotImplementedAlert
import com.cold0.crier.social.R
import com.cold0.crier.social.data.DummyData.getRandomPost
import com.cold0.crier.social.model.ImageHolder
import com.cold0.crier.social.model.Post
import com.cold0.crier.social.theme.ColorUtils.grayed
import com.cold0.crier.social.theme.CrierSocialTheme
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@Composable
fun PostLayout(post: Post) {
    Row(
        modifier = Modifier
            .padding(all = 10.dp)
            .background(MaterialTheme.colors.surface)
    ) {
        PostUserAvatar(post)
        Spacer(modifier = Modifier.size(12.dp))
        Column {
            PostUserInfo(post)
            PostContent(post)
            Spacer(modifier = Modifier.size(4.dp))
            PostActions(post)
        }
    }
}

@Composable
private fun PostUserAvatar(post: Post) {
    val user = post.getUser()
    var notImplementedAlertShow by remember { mutableStateOf(false) }
    if (notImplementedAlertShow) {
        NotImplementedAlert { notImplementedAlertShow = false }
    }
    Image(
        painter = rememberImagePainter(
            data = user.avatar.getDataForPainter(),
            builder = {
                crossfade(true)
            }
        ),
        "",
        modifier = Modifier
            .size(50.dp)
            .clip(shape = CircleShape)
            .clickable(onClick = { notImplementedAlertShow = true }),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun PostUserInfo(post: Post) {
    val user = post.getUser()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = user.name,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
        )
        if (user.verified) {
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                Icons.Filled.VerifiedUser,
                "",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.size(5.dp))
        Text(text = "@${user.handle}", color = MaterialTheme.colors.onSurface.grayed(), maxLines = 1)
    }
}

@Composable
private fun PostContent(post: Post) {
    var numberClickable = 0
    val textAnnotated = buildAnnotatedString {
        val splits = post.content.split("#") // Split text with # so all odd index start with a #hashtag
        splits.forEachIndexed { i, split ->
            if (i % 2 == 1) {
                val splitWhitespace = split.split("\\s+".toRegex()) // Split text with whitespace
                pushStringAnnotation(
                    tag = "$numberClickable",
                    annotation = splitWhitespace[0]
                )
                numberClickable++
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)) {
                    append("#${splitWhitespace[0]}")
                }
                pop()
                if (splitWhitespace.size > 1) { // Even so don't contain #hashtag
                    withStyle(style = SpanStyle(color = MaterialTheme.colors.onSurface)) {
                        append(" " + splitWhitespace.subList(1, splitWhitespace.size).joinToString(" "))
                    }
                }
            } else {
                withStyle(style = SpanStyle(color = MaterialTheme.colors.onSurface)) {
                    append(split)
                }
            }
        }
    }
    val context = LocalContext.current
    ClickableText(
        textAnnotated,
        onClick = { offset ->
            for (i in 0..numberClickable) {
                textAnnotated.getStringAnnotations(
                    tag = "$i",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let { annotation ->
                    Toast.makeText(context, "Clicked on : ${annotation.item}", Toast.LENGTH_SHORT).show();
                }
            }
        }
    )
    if (post.imageList.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        MultipleImageLayout(imageList = post.imageList)
    }
}

@Composable
private fun MultipleImageLayout(imageList: List<ImageHolder>) {
    if (imageList.isEmpty())
        return

    val painterList = imageList.map {
        rememberImagePainter(
            data = it.getDataForPainter(),
            builder = {
                crossfade(true)
            })
    }

    Box(Modifier.clip(shape = RoundedCornerShape(16.dp))) {
        when {
            imageList.size == 1 -> {
                ImageLayout(image = imageList[0], painter = painterList[0])
            }
            imageList.size == 2 -> {
                ImageGridLayout2(imageList = imageList, painterList = painterList)
            }
            imageList.size == 3 -> {
                ImageGridLayout3(imageList = imageList, painterList = painterList)
            }
            imageList.size == 4 -> {
                ImageGridLayout4(imageList = imageList, painterList = painterList)
            }
            imageList.size > 4 -> {
                ImageListLayout(imageList = imageList, painterList = painterList)
            }
        }
    }
}

@Composable
private fun ImageLayout(image: ImageHolder, painter: Painter) {
    Image(
        painter = painter,
        "",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(image.width / image.height.toFloat())
            .background(image.colorAverage),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun ImageGridLayout2(imageList: List<ImageHolder>, painterList: List<Painter>) {
    Row(horizontalArrangement = Arrangement.Center) {
        Image(
            painter = painterList[0],
            "",
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(200.dp)
                .padding(end = 2.dp)
                .background(imageList[0].colorAverage),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterList[1],
            "",
            modifier = Modifier
                .fillMaxWidth(1.0f)
                .height(200.dp)
                .padding(start = 2.dp)
                .background(imageList[1].colorAverage),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ImageGridLayout3(imageList: List<ImageHolder>, painterList: List<Painter>) {
    Row(horizontalArrangement = Arrangement.Center) {
        Image(
            painter = painterList[0],
            "",
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(200.dp)
                .padding(end = 2.dp)
                .background(imageList[0].colorAverage),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(1.0f)
                .height(200.dp)
                .padding(start = 2.dp)
        ) {
            Image(
                painter = painterList[1],
                "",
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(0.5f)
                    .padding(bottom = 2.dp)
                    .background(imageList[1].colorAverage),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = painterList[2],
                "",
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(1.0f)
                    .padding(top = 2.dp)
                    .background(imageList[2].colorAverage),
                contentScale = ContentScale.Crop
            )
        }

    }
}

@Composable
private fun ImageGridLayout4(imageList: List<ImageHolder>, painterList: List<Painter>) {
    Row(horizontalArrangement = Arrangement.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(200.dp)
                .padding(end = 2.dp)
        ) {
            Image(
                painter = painterList[0],
                "",
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(0.5f)
                    .padding(bottom = 2.dp)
                    .background(imageList[1].colorAverage),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = painterList[1],
                "",
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(1.0f)
                    .padding(top = 2.dp)
                    .background(imageList[2].colorAverage),
                contentScale = ContentScale.Crop
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(1.0f)
                .height(200.dp)
                .padding(start = 2.dp)
        ) {
            Image(
                painter = painterList[2],
                "",
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(0.5f)
                    .padding(bottom = 2.dp)
                    .background(imageList[1].colorAverage),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = painterList[3],
                "",
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(1.0f)
                    .padding(top = 2.dp)
                    .background(imageList[2].colorAverage),
                contentScale = ContentScale.Crop
            )
        }

    }
}

@Composable
private fun ImageListLayout(imageList: List<ImageHolder>, painterList: List<Painter>) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0.5f, 0.5f, 0.5f, .1f))
    ) {
        val pagerState = rememberPagerState()
        HorizontalPager(
            count = imageList.size,
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            Image(
                painter = painterList[page],
                "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(imageList[page].colorAverage),
                contentScale = ContentScale.Crop
            )
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun PostActions(cri: Post) {
    var notImplementedAlertShow by remember { mutableStateOf(false) }
    if (notImplementedAlertShow) {
        NotImplementedAlert { notImplementedAlertShow = false }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            IconButton(
                onClick = { notImplementedAlertShow = true },
            ) {
                Icon(
                    Icons.Outlined.Comment,
                    contentDescription = null//TODO
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = cri.commentsCount.toString(),
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            )
        }
        Row {
            IconButton(
                onClick = { notImplementedAlertShow = true },
            ) {
                Icon(
                    painterResource(R.drawable.ic_reblog),
                    tint = if (cri.rebloged) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    contentDescription = null, //TODO,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = cri.reblogCount.toString(),
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            )
        }
        Row {
            IconButton(
                onClick = { notImplementedAlertShow = true },
            ) {
                Icon(
                    if (cri.liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    tint = if (cri.liked) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    contentDescription = null//TODO
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = cri.likeCount.toString(),
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            )
        }
        Row {
            IconButton(
                onClick = { notImplementedAlertShow = true },
            ) {
                Icon(
                    Icons.Outlined.Share,
                    contentDescription = null//TODO
                )
            }
        }
    }
}

// ---------------------------------------------------------------
// COMPOSE PREVIEW
// ---------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun MainPreview() {
    CrierSocialTheme {
        PostLayout(getRandomPost())
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreviewDark() {
    CrierSocialTheme(true) {
        PostLayout(getRandomPost())
    }
}