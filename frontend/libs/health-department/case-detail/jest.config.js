module.exports = {
  name: 'health-department-case-detail',
  preset: '../../../jest.config.js',
  coverageDirectory: '../../../coverage/libs/health-department/case-detail',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};
