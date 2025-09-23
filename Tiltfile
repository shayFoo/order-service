# Build
custom_build(
    # Name of the container image
    ref = 'order-service',
    # Command to build the image - using env parameter for cross-platform compatibility
    command='gradlew bootBuildImage --imageName %EXPECTED_REF%',
    # files to watch for changes
    deps=['build.gradle.kts', 'src']
)

# Deploy
k8s_yaml(['k8s/deployment.yaml', 'k8s/service.yaml'])

# Manage
k8s_resource(
    'order-service',
    port_forwards=['9002']
)