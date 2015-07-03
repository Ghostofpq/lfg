// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.controllers' is found in controllers.js
angular.module('lfg', ['ionic', 'lfg.controllers', 'lfg.rest'])

    .run(function ($ionicPlatform) {
        $ionicPlatform.ready(function () {
            // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
            // for form inputs)
            if (window.cordova && window.cordova.plugins.Keyboard) {
                cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
            }
            if (window.StatusBar) {
                // org.apache.cordova.statusbar required
                StatusBar.styleDefault();
            }
        });
    })

    .config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider

            .state('app', {
                url: "/app",
                abstract: true,
                templateUrl: "templates/menu.html",
                controller: 'AppCtrl'
            })
            .state('login', {
                url: "/login",
                templateUrl: "templates/login.html",
                controller: 'LoginCtrl'
            })

            .state('app.search', {
                url: "/search",
                views: {
                    'menuContent': {
                        templateUrl: "templates/search.html"
                    }
                }
            })

            .state('app.browse', {
                url: "/browse",
                views: {
                    'menuContent': {
                        templateUrl: "templates/browse.html"
                    }
                }
            })
            .state('app.playlists', {
                url: "/playlists",
                views: {
                    'menuContent': {
                        templateUrl: "templates/playlists.html",
                        controller: 'PlaylistsCtrl'
                    }
                }
            })

            .state('app.single', {
                url: "/playlists/:playlistId",
                views: {
                    'menuContent': {
                        templateUrl: "templates/playlist.html",
                        controller: 'PlaylistCtrl'
                    }
                }
            });
        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise('/app/playlists');
    })

    .provider("$auth", function () {
        var user = undefined;
        return {
            $get: function ($location, $lfgRest) {
                return {
                    user: function () {
                        console.log("$auth.user");
                        return user;
                    },
                    isLoggedIn: function () {
                        var result = typeof user != "undefined";
                        console.log("$auth.isLoggedIn : " + result);
                        return result;
                    },
                    logIn: function () {
                        console.log("$auth.logIn");
                        if ($lfgRest.hasToken()) {
                            return $lfgRest.getUser();
                        }
                    },
                    createToken: function (login, password) {
                        console.log("$auth.createToken");
                        $lfgRest.createToken(login, password)
                            .success(function (res) {
                                $lfgRest.setToken(res);
                                $lfgRest.getUser()
                                    .success(function (getUserResponse) {
                                        user=getUserResponse;
                                        return true;
                                    }).error(function (getUserError) {
                                        console.log(getUserError);
                                        return false;
                                    });
                                ;
                            }).error(function (err) {
                                console.log(err);
                                return false;
                            });
                    },
                    logOut: function () {
                        console.log("$auth.logOut");
                        user = undefined;
                        $location.path("/app/login");
                    },
                    register: function (login, password, email) {
                        console.log("$auth.register");
                        $lfgRest.registerUser(login, password, email)
                            .success(function (res) {
                                createToken(res.login, res.password);
                            }).error(function (err) {
                                console.log(err);
                            });
                    }
                };
            }
        };
    });


