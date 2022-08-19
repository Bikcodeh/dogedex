[![kotlin](https://img.shields.io/github/languages/top/bikcodeh/ToDoApp.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/) [![Android API](https://img.shields.io/badge/api-23%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=23)

# Dog Recognizer

### This application was developed by modules feature.

## :star: Features

- [x] Display dogs previously save in the api
- [x] Set dogs as favorite
- [x] Display favorite dogs
- [x] Register
- [x] Login
- [x] Logout
- [x] Scan dog with camera x
- [x] Dog detail

:runner: For run the app just clone the repository and execute the app on Android Studio.

### Requirements to install the app
- Use phones with Android Api 23+
- Having an internet connection

##### This application was developed using Kotlin and uses the following components:
- Kotlin based, Coroutines + Flow for asynchronous.
- Coroutines
- Clean architecture (Domain, Data, Presentation)
- MVVM
- Repository pattern
- StateFlow
- Navigation component
- Dagger Hilt (Dependency injection)
- Unit testing (Truth by google, coroutines tests)
- Moshi - A modern JSON library for Kotlin and Java.
- Retrofit2 & OkHttp3 - Construct the REST APIs.
- Data store
- Camera X
- Permissions
- Coil images

## Screenshots Light theme
 | Home |     Detail    |  Favorite  |   Log in    |  Sign in |   Splash |
 | :----: | :---------: | :-------: | :-----------: | :-------:| :-------:|
 |![Home](assets/home.png?raw=true)|![Detail](assets/detail.png?raw=true)|![Favorite](assets/favorite.png?raw=true)|![Login](assets/login.png?raw=true)|![Sign in](assets/register.png?raw=true)|![Splash](assets/splash.png?raw=true)|

## :dart: Architecture

The application is built using Clean Architeture pattern based on [Architecture Components](https://developer.android.com/jetpack/guide#recommended-app-arch) on Android. The application is divided into three layers:

![Clean Arquitecture](https://devexperto.com/wp-content/uploads/2018/10/clean-architecture-own-layers.png)

https://user-images.githubusercontent.com/24237865/77502018-f7d36000-6e9c-11ea-92b0-1097240c8689.png

- Domain: This layer contains the business logic of the application, here we define the data models and the use cases.
- Data: This layer contains the data layer of the application. It contains the database, network and the repository implementation.
- UI: This layer contains the presentation layer of the application like fragment, activity, viewmodel etc.


**Bikcodeh**
