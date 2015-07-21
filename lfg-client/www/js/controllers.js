angular.module('lfg.controllers', [])

.controller('AppCtrl', function ($scope, $ionicModal, $timeout, $lfgRest, $location) {
    console.log("AppCtrl");
    if (!$lfgRest.isLoggedIn()) {
        $location.path("/login");
    }
    $scope.logOut = function () {
        $lfgRest.logOut();
        $location.path("/login");
    };
    // With the new view caching in Ionic, Controllers are only called
    // when they are recreated or on app start, instead of every page change.
    // To listen for when this page is active (for example, to refresh data),
    // listen for the $ionicView.enter event:
    //$scope.$on('$ionicView.enter', function(e) {
    //});

    // Form data for the login modal
    //LOGIN
    // $scope.loginData = {};
    // $ionicModal.fromTemplateUrl('templates/loginmodal.html', {
    //     scope: $scope
    // }).then(function (modal) {
    //     $scope.modalLogin = modal;
    // });
    // $scope.closeLogin = function () {
    //     $scope.modalLogin.hide();
    // };
    // $scope.login = function () {
    //     $auth.logIn();
    //     if (!$auth.isLoggedIn()) {
    //         $scope.modalLogin.show();
    //     }
    // };
    // $scope.doLogin = function () {
    //     console.log('Doing login', $scope.loginData);
    //     $auth.createToken($scope.loginData.login, $scope.loginData.password);
    // };
    // //REGISTER
    // $scope.registerData = {};
    // $ionicModal.fromTemplateUrl('templates/register.html', {
    //     scope: $scope
    // }).then(function (modal) {
    //     $scope.modalRegister = modal;
    // });
    // $scope.closeRegister = function () {
    //     $scope.modalRegister.hide();
    // };
    // $scope.register = function () {
    //     $scope.modalRegister.show();
    // };
    // $scope.doRegister = function () {
    //     console.log('Doing register', $scope.registerData);
    //     $auth.register($scope.registerData.login, $scope.registerData.password, $scope.registerData.email);
    // };

})

.controller('LoginCtrl', function ($scope, $ionicModal, $timeout, $lfgRest, $state) {
    console.log("LoginCtrl");

    if ($lfgRest.hasToken()) {
        console.log("context has token");
        $lfgRest.getUser()
            .success(function (res) {
                $lfgRest.setUserLocal(res);
                $state.go('app.playlists');
            })
            .error(function (err) {
                $scope.error = err.status + " : " + err.error;
            });
    }

    console.log("context has no token");
    $scope.loginData = {};
    $scope.doLogin = function () {
        console.log('Doing login', $scope.loginData);
        var login = $scope.loginData.login;
        var pass = $scope.loginData.password;

        $scope.loginData.password = "";

        $lfgRest.createToken(login, pass)
            .success(function (res) {
                console.log(res);
                $lfgRest.setToken(res);
                $lfgRest.getUser()
                    .success(function (getUserResponse) {
                        $lfgRest.setUserLocal(res);
                        $state.go('app.playlists');
                    })
                    .error(function (getUserError) {
                        console.log(getUserError);
                        $scope.error = err.message;
                    });
            })
            .error(function (err) {
                console.log(err);
                $scope.error = err.message;
            });
    };
    $scope.goToSignUp = function () {
        $state.go('signup');
    };
})

.controller('SignUpCtrl', function ($scope, $ionicModal, $timeout, $lfgRest, $state) {
    console.log("SignUpCtrl");
    $scope.signUpData = {};
    $scope.doSignUp = function () {
        console.log('Doing SignUp', $scope.signUpData);
        if (!($scope.signUpData.password === $scope.signUpData.repeatPassword)) {
            $scope.signUpData.password = "";
            $scope.signUpData.repeatPassword = "";
            $scope.error = "Password and Repeat Password should be the same";
        } else {
            var login = $scope.signUpData.login;
            var pass = $scope.signUpData.password;
            var email = $scope.signUpData.email;
            $scope.signUpData.password = "";
            $scope.signUpData.repeatPassword = "";
            $lfgRest.registerUser(login, pass, email)
                .success(function (registerUserResponse) {
                    $lfgRest.setUserLocal(registerUserResponse);
                    $lfgRest.createToken(login, pass)
                        .success(function (createTokenResponse) {
                            console.log(createTokenResponse);
                            $lfgRest.setToken(createTokenResponse);
                            $state.go('app.playlists');
                        })
                        .error(function (err) {
                            console.log(err);
                            $scope.error = err.message;
                        });
                })
                .error(function (registerUserErr) {
                    console.log(registerUserErr);
                    $scope.error = registerUserErr.message;
                });
        }
    };
    $scope.goToLogin = function () {
        $state.go('login');
    };
})

.controller('PlaylistsCtrl', function ($scope) {
    $scope.playlists = [
        {
            title: 'Reggae',
            id: 1
        },
        {
            title: 'Chill',
            id: 2
        },
        {
            title: 'Dubstep',
            id: 3
        },
        {
            title: 'Indie',
            id: 4
        },
        {
            title: 'Rap',
            id: 5
        },
        {
            title: 'Cowbell',
            id: 6
        }
        ];
})

.controller('PlaylistCtrl', function ($scope, $stateParams) {});
